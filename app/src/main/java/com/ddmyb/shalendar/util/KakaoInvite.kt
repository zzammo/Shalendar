package com.ddmyb.shalendar.util
import android.content.ActivityNotFoundException
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.link.LinkClient
import com.kakao.sdk.link.WebSharerClient
import com.kakao.sdk.template.model.*
class KakaoInvite(private val context: Context) {
    fun sendKakaoLink(title: String, desc: String, image: String) {
        // 피드 템플릿 생성
        val defaultFeed = FeedTemplate(
            content = Content(
                title = title,
                description = desc,
                imageUrl = image,
                link = Link()
            ),
            buttons = listOf(
                Button(
                    "자세히 보기",
                    Link(
                        androidExecutionParams = mapOf(
                            "productIdx" to "123", // productIdx를 적절한 값으로 변경 필요
                            "pay_mode" to "benefit"
                        )
                    )
                )
            )
        )

        // 피드 메시지 보내기
        if (context.let { LinkClient.instance.isKakaoLinkAvailable(context) }) {
            // 카카오톡으로 카카오링크 공유 가능
            context.let {
                LinkClient.instance.defaultTemplate(context, defaultFeed) { linkResult, error ->
                    if (error != null) {
                        Log.e("TAG", "카카오링크 보내기 실패", error)
                    } else if (linkResult != null) {
                        Log.e("TAG", "카카오링크 보내기 성공 ${linkResult.intent}")
                        context.startActivity(linkResult.intent) //카카오톡이 깔려있을 경우 카카오톡으로 넘기기

                        // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않음
                        Log.e("TAG", "Warning Msg: ${linkResult.warningMsg}")
                        Log.e("TAG", "Argument Msg: ${linkResult.argumentMsg}")
                    }
                }
            }
        } else {  // 카카오톡 미설치: 웹 공유 사용 권장
            // 웹 공유 예시 코드
            val sharerUrl = WebSharerClient.instance.defaultTemplateUri(defaultFeed)

            // 1. CustomTabs으로 Chrome 브라우저 열기
            try {
                context.let { KakaoCustomTabsClient.openWithDefault(context, sharerUrl) }
            } catch (e: UnsupportedOperationException) {
                // Chrome 브라우저가 없을 때
                Toast.makeText(context, "chrome 또는 인터넷 브라우저를 설치해주세요", Toast.LENGTH_SHORT).show()
            }

            // 2. CustomTabs으로 디바이스 기본 브라우저 열기
            try {
                context.let { KakaoCustomTabsClient.open(context, sharerUrl) }
            } catch (e: ActivityNotFoundException) {
                // 인터넷 브라우저가 없을 때
                Toast.makeText(context, "chrome 또는 인터넷 브라우저를 설치해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
