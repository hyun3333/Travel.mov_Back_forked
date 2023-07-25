package com.tramovel.tour.user.api;

import com.tramovel.tour.coupon.dto.request.CouponCreateDTO;
import com.tramovel.tour.coupon.service.CouponService;
import com.tramovel.tour.user.dto.request.UserDeleteRequestDTO;
import com.tramovel.tour.user.dto.request.UserLoginRequestDTO;
import com.tramovel.tour.user.dto.request.UserModifyRequestDTO;
import com.tramovel.tour.user.dto.request.UserSignUpRequestDTO;
import com.tramovel.tour.user.dto.response.UserLoginResponseDTO;
import com.tramovel.tour.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private CouponService couponService;

  // 회원가입
  @PostMapping
  public void signup(
    @Validated @RequestBody UserSignUpRequestDTO dto,
//    @RequestPart(value = "profileImage", required = false) MultipartFile profileImg,
    BindingResult result
  ) {

    if(result.hasErrors()) { // 입력값 검증에 걸림
      List<FieldError> fieldErrors = result.getFieldErrors();
      fieldErrors.forEach(err -> {
        log.warn("invalid client data - {}", err.toString());
      });

      // 회원가입시 프로필 사진 받는거 잠시 보류함
//      return ResponseEntity
//        .badRequest()
//        .body(fieldErrors);
    }

//    try {
//      String uploadedFilePath = null;
//      if(profileImg != null) {
//        uploadedFilePath = userService.uploadProfileImage(profileImg);
//      }
      userService.signup(dto, "");
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
    CouponCreateDTO createCoupon = CouponCreateDTO.builder()
      .userId(dto.getId())
      .discountPrice(10000)
      .name("회원 가입 10000원 쿠폰")
      .build();
    couponService.createCoupon(createCoupon);

  }

  //아이디 중복 확인 요청처리
  @GetMapping("/check")
  public ResponseEntity<?> check(String id) {
    if(id.equals("")) {
      return ResponseEntity.badRequest()
              .body("아이디가 없습니다!");
    }
    boolean result = userService.isDuplicate(id); // 중복되었니?

    return ResponseEntity.ok().body(result);
  }

  // 로그인할 때 이메일 정보 빼오기
  @PostMapping("/getEmail")
  public String getEmail(String id) {

    return "";
  }

  //로그인 요청 처리
  @PostMapping("/signin")
  public ResponseEntity<?> signIn(
    @Validated @RequestBody UserLoginRequestDTO dto,   //json으로 넘어오는 데이터는 @requestbody로 자바객체로 변환.
    BindingResult result
  ) {
    if(result.hasErrors()) { // 입력값 검증에 걸림
      List<FieldError> fieldErrors = result.getFieldErrors();
      fieldErrors.forEach(err -> {
        log.warn("invalid client data - {}", err.toString());
      });

      return ResponseEntity
        .badRequest()
        .body(fieldErrors);
    }

    try {
      UserLoginResponseDTO responseDTO
        = userService.authenticate(dto);

      return ResponseEntity.ok().body(responseDTO);

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.badRequest()
        .body(e.getMessage());
    }
  }

  //회원 정보 수정
  @PutMapping
  public void modify(
    @Validated @RequestPart(value = "user") UserModifyRequestDTO dto,
    @RequestPart(value = "profileImage", required = false) MultipartFile profileImg,
    BindingResult result
  ) {
    if(result.hasErrors()) { // 입력값 검증에 걸림
      List<FieldError> fieldErrors = result.getFieldErrors();
      fieldErrors.forEach(err -> {
        log.warn("invalid client data - {}", err.toString());
      });

//      return ResponseEntity
//        .badRequest()
//        .body(fieldErrors);
    }

    try {
      String uploadedFilePath = null;
      if(profileImg != null) {
        uploadedFilePath = userService.uploadProfileImage(profileImg);
      }
      userService.modify(dto, uploadedFilePath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  //회원 탈퇴
  @DeleteMapping
  public void delete(
    @Validated @RequestBody UserDeleteRequestDTO dto,
    BindingResult result
    ) {
    if(result.hasErrors()) { // 입력값 검증에 걸림
      List<FieldError> fieldErrors = result.getFieldErrors();
      fieldErrors.forEach(err -> {
        log.warn("invalid client data - {}", err.toString());
      });

//      return ResponseEntity
//        .badRequest()
//        .body(fieldErrors);
    }

    userService.delete(dto);
  }

}
