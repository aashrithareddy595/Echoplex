package com.example.echoplex.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.echoplex.R
import com.example.echoplex.databinding.DeleteLayoutBinding
import com.example.echoplex.databinding.ReceiveMsgBinding
import com.example.echoplex.databinding.SendMsgBinding
import com.example.echoplex.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MessagesAdapter(
    var context: Context,
    var messages: ArrayList<Message>,
    var senderRoom: String,
    var receiverRoom: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_SENT = 1
    private val ITEM_RECEIVE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SENT) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.send_msg, parent, false)
            SentMsgHolder(view)
        } else {
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive_msg, parent, false)
            ReceiveMsgHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (FirebaseAuth.getInstance().uid == message.senderId) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMsgHolder) {
            if (message.message == "photo") {
                holder.binding.image.visibility = View.VISIBLE
                holder.binding.message.visibility = View.GONE
                holder.binding.mLinear.visibility = View.GONE
                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.binding.image)
            } else {
                holder.binding.image.visibility = View.GONE
                holder.binding.message.visibility = View.VISIBLE
                holder.binding.mLinear.visibility = View.VISIBLE
                holder.binding.message.text = message.message
            }
            holder.itemView.setOnLongClickListener {
                showDeleteDialog(message, true)
                true
            }
        } else if (holder is ReceiveMsgHolder) {
            if (message.message == "photo") {
                holder.binding.image.visibility = View.VISIBLE
                holder.binding.message.visibility = View.GONE
                holder.binding.mLinear.visibility = View.GONE
                Glide.with(context)
                    .load(message.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.binding.image)
            } else {
                holder.binding.image.visibility = View.GONE
                holder.binding.message.visibility = View.VISIBLE
                holder.binding.mLinear.visibility = View.VISIBLE
                holder.binding.message.text = message.message
            }
            holder.itemView.setOnLongClickListener {
                showDeleteDialog(message, false)
                true
            }
        }
    }

    private fun showDeleteDialog(message: Message, isSender: Boolean) {
        val view = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
        val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)
        val dialog = AlertDialog.Builder(context)
            .setTitle("Delete Message")
            .setView(binding.root)
            .create()

        binding.everyone.setOnClickListener {
            message.message = "This message is removed"
            message.messageId?.let {
                FirebaseDatabase.getInstance().reference.child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(it).setValue(message)
            }
            message.messageId?.let {
                FirebaseDatabase.getInstance().reference.child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(it).setValue(message)
            }
            dialog.dismiss()
        }

        binding.delete.setOnClickListener {
            message.messageId?.let {
                FirebaseDatabase.getInstance().reference.child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(it).setValue(null)
            }
            dialog.dismiss()
        }

        binding.cancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    inner class SentMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: SendMsgBinding = SendMsgBinding.bind(itemView)
    }

    inner class ReceiveMsgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ReceiveMsgBinding = ReceiveMsgBinding.bind(itemView)
    }
}
