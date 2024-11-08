package com.dmribeiro.cmpmapview

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyle
import platform.UIKit.UIApplication

actual fun showToast(message: String) {
    MainScope().launch {
        val alert = UIAlertController.alertControllerWithTitle(
            title = null,
            message = message,
            preferredStyle = UIAlertControllerStyle.MAX_VALUE
        )

        UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
            viewControllerToPresent = alert,
            animated = true,
            completion = {
                alert.dismissViewControllerAnimated(true, null)
            }
        )

    }
}
