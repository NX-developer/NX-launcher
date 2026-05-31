package com.mio.ui.dialog

import android.content.Context
import android.graphics.Point
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import com.tungsten.fcl.R
import com.tungsten.fcl.databinding.DialogSelectRendererBinding
import com.tungsten.fcl.setting.Profiles
import com.tungsten.fclauncher.FCLConfig
import com.tungsten.fclauncher.plugins.DriverPlugin
import com.tungsten.fclauncher.plugins.RendererPlugin
import com.tungsten.fcllibrary.component.dialog.FCLDialog
import com.tungsten.fcllibrary.util.ConvertUtils
import java.util.function.Consumer
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.tungsten.fcl.activity.MainActivity.Companion.getInstance
import com.tungsten.fcl.util.RequestCodes
import com.tungsten.fcllibrary.browser.FileBrowser
import com.tungsten.fcllibrary.browser.options.LibMode
import com.tungsten.fcllibrary.browser.options.SelectionMode
import java.io.File

class DriverSelectDialog(
    context: Context,
    val isGlobal: Boolean,
    val callback: Consumer<String>
) : FCLDialog(context) {

    init {
        val point = Point()
        window?.windowManager?.defaultDisplay?.getSize(point)
        val params = window?.attributes
        params?.width = ConvertUtils.dip2px(context, 500f)
        val ratio = point.x.toFloat() / point.y.toFloat()
        if (ratio >= 1.5f) {
            params?.height = WindowManager.LayoutParams.MATCH_PARENT
        } else {
            params?.height = point.y * 1 / 2
        }
        window?.attributes = params
        val binding = DialogSelectRendererBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.title.text = context.getString(R.string.settings_fcl_driver)
        binding.listView.adapter =
            ArrayAdapter(context, R.layout.item_renderer, mutableListOf<String>().apply {
                DriverPlugin.driverList.forEach {
                    add(it.driver)
                }
                add(context.getString(R.string.settings_driver_local_so))
            })
        binding.listView.setOnItemClickListener { _, _, position, _ ->
            if (position >= DriverPlugin.driverList.size) {
                dismiss()
                val builder = FileBrowser.Builder(context)
                val suffix = ArrayList<String?>()
                suffix.add(".so")
                builder.setLibMode(LibMode.FILE_CHOOSER)
                builder.setSelectionMode(SelectionMode.SINGLE_SELECTION)
                builder.setSuffix(suffix)
                builder.create().browse(getInstance(), RequestCodes.SELECT_VERSION_ICON_CODE) { _: Int, resultCode: Int, data: Intent? ->
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        if (FileBrowser.getSelectedFiles(data).isEmpty()) return@browse
                        val path = FileBrowser.getSelectedFiles(data)[0]
                        val message = if (DriverPlugin.importCustomDriver(context, File(path)))
                            R.string.settings_driver_local_ok else R.string.settings_driver_local_fail
                        Toast.makeText(context, context.getString(message), Toast.LENGTH_SHORT).show()
                    }
                }
                return@setOnItemClickListener
            }
            val versionSetting =
                if (isGlobal) Profiles.getSelectedProfile().global else Profiles.getSelectedProfile().versionSetting
            versionSetting.driver = DriverPlugin.driverList[position].driver
            DriverPlugin.selected = DriverPlugin.driverList[position]
            dismiss()
            callback.accept(binding.listView.adapter.getItem(position).toString())
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
    }
}