package com.ddmyb.shalendar.domain.users

//사용자 계정 정보 모델 클라스
class UserAccount {
    @JvmField
    var nickName: String? = null    //닉네임

    @JvmField
    var userId: String? = null //고유토큰

    @JvmField
    var emailId: String? = null //이메일 아이디

    @JvmField
    var password: String? = null //패스워드

    @JvmField
    var groupId: MutableList<String> = mutableListOf() //속한 GroupdId 리스트

    @JvmField
    var pfImage: String? = null //프로필 이미지

}
