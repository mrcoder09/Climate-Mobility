package com.cityof.glendale.network.responses

import com.google.gson.annotations.SerializedName

data class SocialMediaTemplateResponse(
    @SerializedName("customcode") val customCode: Int?,
    @SerializedName("success") val success: Boolean?,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: SocialMediaTemplate
)

data class SocialMediaTemplate(
    @SerializedName("facebook_post_template") val facebookPostTemplate: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("instagram_post_template") val instagramPostTemplate: String? = null,
    @SerializedName("twitter_post_template") val twitterPostTemplate: String? = null
)


fun SocialMediaTemplateResponse.isSuccess() = (isSuccess(customCode) && success ?: false)




