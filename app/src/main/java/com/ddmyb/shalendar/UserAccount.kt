package com.ddmyb.shalendar

/**
 * 사용자 계정 정보 모델 클라스
 */
class UserAccount {
    @JvmField
    var idToken: String? = null //고유토큰
    @JvmField
    var emailId: String? = null //이메일 아이디
    @JvmField
    var password: String? = null //패스워드
}