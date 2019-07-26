package com.tykj.wx.controller;


import com.jfinal.core.Controller;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.exception.BusinessException;
import com.tykj.job.ListenQueueThread;
import com.tykj.job.MoveQrParamThread;
import com.tykj.wx.dto.UserInfoDTO;

import com.tykj.wx.service.IJobParamRecordService;
import com.tykj.wx.service.ITmpQrcodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author huran
 * @since 2019-05-11
 */
@Api(tags = "二维码")
@Slf4j
@RestController
@RequestMapping("/rest/wx/qrcode")
public class QrcodeController extends Controller {
    @Autowired
    private ITmpQrcodeService tmpQrcodeService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IJobParamRecordService jobParamRecordService;
    //监听队列的线程
    private static ListenQueueThread listenQueueThread = null;
    //保存移动记录
    private static volatile LinkedBlockingQueue queue = new LinkedBlockingQueue(1000);
    //移动二维码线程池
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()
            , Runtime.getRuntime().availableProcessors() * 2, 0L, TimeUnit.MILLISECONDS, new
            LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
        final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(Thread.currentThread().getThreadGroup(), r, "移动文件-thread" + (this.threadNumber
                    .getAndIncrement()));
            t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    log.info("线程:[{}],异常信息[{}]", t.getName(), e.getMessage());
                }
            });
            return t;
        }
    }) {
    };

    /**
     * 判断用户是否绑定二维码信息
     *
     * @param qrParam 二维码UUID
     * @param openId  用户标识
     * @return ApiResponse
     * @throws Exception
     */
    @ApiOperation(value = "判断用户是否绑定二维码信息", notes = "判断用户是否绑定二维码信息")
    @GetMapping(value = "/bindingUserInfo")
    public ApiResponse isbindingUserInfo(@RequestParam(value = "qrParam") String qrParam, @RequestParam(value =
            "openId") String openId) throws Exception {
        ApiResponse apiResponse = this.tmpQrcodeService.isbindingUserInfo(qrParam, openId);
        return apiResponse;
    }

    /**
     * 绑定用户信息 ---线下扫描的
     *
     * @param userInfoDTO   用户信息
     * @param bindingResult 校验用户信息
     * @return ApiResponse
     * @throws Exception
     */
    @ApiOperation(value = "绑定用户信息", notes = "绑定用户信息")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping(value = "/bindingQr")
    public ApiResponse bindingQr(@RequestBody @Valid UserInfoDTO userInfoDTO, BindingResult bindingResult) throws
            Exception {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().stream().forEach(fieldError -> {
                log.info("==============>>>" + fieldError.getDefaultMessage());
                throw new BusinessException(ApiCode.EMPTY_PARAM, fieldError.getDefaultMessage());
            });
        }
        ApiResponse apiResponse = null;
        try {
            apiResponse = this.tmpQrcodeService.editOrSave(userInfoDTO);
            this.threadPoolExecutor.execute(new MoveQrParamThread(userInfoDTO.getQrParam(), this.stringRedisTemplate,
                    this.jobParamRecordService, queue));
        } catch (Exception e) {
            log.error("绑定用户信息异常[{}]", e.getMessage());
        }
        return apiResponse;
    }

    /**
     * 开启或者关闭通知
     *
     * @param qrParam  二维码UUID
     * @param openId   用户标识
     * @param isSwitch 1 开启 2 关闭
     * @return ApiResponse
     * @throws Exception
     */
    @GetMapping(value = "/onOrOffQr")
    public ApiResponse onOrOffQr(@RequestParam(value = "qrParam") String qrParam, @RequestParam(value = "openId")
            String openId, @RequestParam(value = "isSwitch") String isSwitch) throws Exception {
        ApiResponse apiResponse = this.tmpQrcodeService.onOrOffQr(qrParam, openId, isSwitch);
        return apiResponse;
    }

    /**
     * 删除体验码
     *
     * @param qrParam 二维码UUID
     * @param openId  用户标识
     * @return ApiResponse
     * @throws Exception
     */
    @GetMapping(value = "deleteTmpQr")
    public ApiResponse deleteTmpQr(@RequestParam(value = "qrParam") String qrParam, @RequestParam(value = "openId")
            String openId) throws Exception {
        log.info("删除体验码参数:[{}],[{}]", qrParam, openId);
        ApiResponse apiResponse = this.tmpQrcodeService.deleteTmpQr(qrParam, openId);
        return apiResponse;
    }


    /**
     * 初始化监听队列
     */
    @PostConstruct
    public void initListenQueue() {
        this.listenQueueThread = new ListenQueueThread(this.queue,this.jobParamRecordService);
        this.listenQueueThread.setDaemon(true);
        this.listenQueueThread.setName("监听队列Thread");
        this.listenQueueThread.start();

    }
}
