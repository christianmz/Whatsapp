package io.github.christianmz.whatsapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.christianmz.whatsapp.R

class ChatsFragment : Fragment() {

    private lateinit var _view: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.fragment_chats, container, false)

        return _view
    }
}
