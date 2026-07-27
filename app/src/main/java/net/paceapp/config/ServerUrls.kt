package net.paceapp.config

object AppWebUrls {

    private const val BASE_URL = "https://paceapp.net"

    // Pages URLs
    const val TERM_OF_SERVICE = "$BASE_URL/terms-of-service"
    const val PRIVACY_POLICY = "$BASE_URL/privacy-policy"
    const val LICENSES = "$BASE_URL/licenses"
    const val DEVELOPER_WEBSITE = "https://wvelabs.com/"

    // FAQ / Help — opened in an in-app Custom Tab (mirrors iOS SafariView). Note the
    // `.php` suffix, unlike the other pages.
    const val FAQ = "$BASE_URL/faq.php"
}
