package io.github.wawakaka.basicframeworkproject.base.composer

import android.content.Intent
import com.trello.navi2.NaviComponent
import com.trello.navi2.component.support.NaviAppCompatActivity
import io.github.wawakaka.basicframeworkproject.base.model.RequestPermissionResultStatus
import io.reactivex.Observer
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by wawakaka on 17/07/18.
 */

open class BaseActivity : NaviAppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        private val TAG = BaseActivity::class.java.simpleName
        const val RC_SETTINGS_SCREEN = 1601
    }

    protected val naviComponent: NaviComponent = this

    private var rationale: String? = null
    private var requestCode = -1
    private var permissions: MutableList<String>? = null
    private var listener: Observer<RequestPermissionResultStatus>? = null

    /**
     * Permissions checking
     *
     * @param permissions List of permissions to be checked
     *
     * @return True if has been granted, false otherwise
     */
    fun hasPermissions(permissions: List<String>): Boolean {
        return EasyPermissions.hasPermissions(
            this, *permissions.toTypedArray()
        )
    }

    /**
     * Request permissions for Android M and higher
     *
     * @param rationale   String reason why user needs to grant this permissions
     * @param requestCode Used for activity result
     * @param permissions List of permissions requested
     */
    fun requestPermissions(rationale: String,
                           requestCode: Int,
                           permissions: MutableList<String>,
                           listener: Observer<RequestPermissionResultStatus>) {
        this.rationale = rationale
        this.requestCode = requestCode
        this.permissions = permissions
        this.listener = listener

        EasyPermissions.requestPermissions(
            this,
            rationale,
            requestCode,
            *permissions.toTypedArray()
        )
    }

    override fun onPermissionsGranted(requestCode: Int, permissions: MutableList<String>) {
        listener?.onNext(RequestPermissionResultStatus(
            requestCode, permissions, RequestPermissionResultStatus.GRANTED
        ))
    }

    override fun onPermissionsDenied(requestCode: Int, permissions: MutableList<String>) {
        listener?.onNext(RequestPermissionResultStatus(
            requestCode, permissions, RequestPermissionResultStatus.DENIED
        ))

        val deniedPermissionNeverAskAgain = EasyPermissions.somePermissionPermanentlyDenied(this, permissions)

        if (deniedPermissionNeverAskAgain) {
            AppSettingsDialog
                .Builder(this)
                .setRequestCode(RC_SETTINGS_SCREEN)
                .build()
                .show()
        }

        if (!deniedPermissionNeverAskAgain) {
            listener?.onNext(RequestPermissionResultStatus(
                this.requestCode, permissions, RequestPermissionResultStatus.DENIED_NEVER_ASK
            ))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SETTINGS_SCREEN) {
            if (permissions?.isEmpty() != false) {
                return
            }

            if (EasyPermissions.hasPermissions(this, *permissions?.toTypedArray() ?: arrayOf())) {
                listener?.onNext(RequestPermissionResultStatus(
                    this.requestCode, permissions, RequestPermissionResultStatus.GRANTED
                ))
            } else {
                listener?.onNext(RequestPermissionResultStatus(
                    this.requestCode, permissions, RequestPermissionResultStatus.DENIED_NEVER_ASK
                ))
            }
        }
    }

}