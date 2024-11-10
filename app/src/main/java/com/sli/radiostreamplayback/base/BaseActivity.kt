package com.sli.radiostreamplayback.base

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.viewbinding.ViewBinding

abstract class BaseActivity : AppCompatActivity() {

    abstract val binding: ViewBinding
    abstract val fragmentContainerView: FragmentContainerView

    fun navigateTo(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(fragmentContainerView.id, fragment, tag)
            .addToBackStack(null)
            .commit()
    }

}