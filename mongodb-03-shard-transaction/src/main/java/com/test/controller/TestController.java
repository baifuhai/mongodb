package com.test.controller;

import com.test.service.MeasureInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore("测试接口")
@RestController
@RequestMapping("/api/test")
public class TestController {

	@Autowired
	MeasureInfoService measureInfoService;

	@ApiOperation("生成数据")
	@PostMapping("generateData")
	public String generateData() {
		measureInfoService.generateData();
		return "ok";
	}

}
