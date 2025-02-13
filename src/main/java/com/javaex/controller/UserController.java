package com.javaex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.javaex.service.UserService;
import com.javaex.util.JsonResult;
import com.javaex.util.JwtUtil;
import com.javaex.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	// 저장
	@PostMapping(value = "/api/users/add")
	public UserVo add(@RequestBody UserVo userVo) {
		System.out.println("UserController.add()");
		System.out.println(userVo);

		UserVo userInfoVo = userService.exeAddandUser(userVo);
		System.out.println(userInfoVo);

		return userInfoVo;
	}

	// 회원정보 수정
	@PutMapping("/api/users/modify")
	public JsonResult modify(@RequestBody UserVo userVo, HttpServletRequest request) {
		System.out.println("UserController.modify()");

		int no = JwtUtil.getNoFromHeader(request);
		if (no != -1) { // 정상
			// db에 수정시킨다
			userService.exeModify(userVo);
			return JsonResult.success(userVo.getName());
		} else {
			return JsonResult.fail("로그인하지않음");
		}

	}

	// 회원정보 수정폼(1명 데이터가져오기)
	@GetMapping("/api/users/modify")
	public JsonResult modifyform(HttpServletRequest request) {
		System.out.println("UserController.modifyform()");

		/*
		 * // 토큰가져오기 String token = JwtUtil.getTokenByHeader(request);
		 * System.out.println("token=" + token); // 검증 boolean check =
		 * JwtUtil.checkToken(token); System.out.println(check);
		 * 
		 * // 이상없음 --> 이상있음 if (check==true) { System.out.println("정상"); int no =
		 * Integer.parseInt(JwtUtil.getSubjectFromToken(token)); System.out.println(no);
		 * }
		 */

		int no = JwtUtil.getNoFromHeader(request);

		if (no != -1) {
			// 정상
			UserVo userVo = userService.exeModifyForm(no);
			System.out.println(userVo);
			return JsonResult.success(userVo);
		} else {
			// 토큰이 없거나(로그인 상태 아님) 변조된 경우
			return JsonResult.fail("토큰X, 비로그인, 변조");
		}

	}

	// 로그인
	@PostMapping("/api/users/login")
	public JsonResult login(@RequestBody UserVo userVo, HttpServletResponse response) {
		System.out.println("UserController.login()");
		System.out.println(userVo);

		// no name id pw
		UserVo authUser = userService.exeLogin(userVo);
		System.out.println(authUser);

		if (authUser != null) { // 로그인에 성공하면
			// 토큰발급 헤더에 실어 보낸다
			JwtUtil.createTokenAndSetHeader(response, "" + authUser.getNo());
			return JsonResult.success(authUser);
		} else {
			return JsonResult.fail("로그인실패");
		}

	}

}
