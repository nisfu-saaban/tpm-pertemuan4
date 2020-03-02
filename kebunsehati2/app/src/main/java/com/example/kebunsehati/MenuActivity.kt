package com.example.kebunsehati

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.example.kebunsehati.Preferences.PreferencesHelper
import com.example.kebunsehati.Preferences.PreferencesKey
import com.example.kebunsehati.adapter.CategoryAdapter
import com.example.kebunsehati.adapter.ItemCategoryAdapter
import com.example.kebunsehati.model.DataItem
import com.example.kebunsehati.model.ProdukItem
import com.example.kebunsehati.model.ProdukResponse
import com.example.kebunsehati.service.Services
import kotlinx.android.synthetic.main.activity_menu.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MenuActivity : AppCompatActivity() {

    lateinit var category: ProdukResponse
    lateinit var categori: ArrayList<ProdukResponse>
    lateinit var produkItem: ArrayList<ProdukItem>
    lateinit var dataProdukItem: ProdukItem
    var posisi: Int? = null
    var count: Int = 1
    private lateinit var pref: PreferencesHelper
    private val logOut by lazy {
        AlertDialog.Builder(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        pref = PreferencesHelper(this)
        bt_chart_detail.setOnClickListener {
            intent(ChartActivity::class.java)
        }

        rv_category.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false
        )

        ib_popup.setOnClickListener { openDrawer() }

        val kategori = pref.getString(PreferencesKey.IDMEMBER)
        val kategoriBody = RequestBody.create("text/plain".toMediaTypeOrNull(), kategori!!)

        Services().apiServices.showListKategoryProduct(kategoriBody)
            .enqueue(object : Callback<ArrayList<ProdukResponse>> {
                override fun onFailure(call: Call<ArrayList<ProdukResponse>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<ArrayList<ProdukResponse>>, response: Response<ArrayList<ProdukResponse>>
                ) {
                    categori = response.body()!!
                    pref.putString(PreferencesKey.LISTPRODUK,response.body().toString())
                    val categoryItem = CategoryAdapter(categori,{ categori, position ->
                        category = categori
                        posisi = position
                    })
                    rv_category.apply {
                        layoutManager = LinearLayoutManager(context,
                            LinearLayoutManager.HORIZONTAL,false)
                        adapter = categoryItem
                    }
                    rv_category.adapter?.notifyDataSetChanged()
                }
            })

        Services().apiServices.tampilDataProduk(kategoriBody).enqueue(object : Callback<ProdukResponse>{
            override fun onFailure(call: Call<ProdukResponse>, t: Throwable) {
                Log.d("error", "${t.message}")
            }

            override fun onResponse(
                call: Call<ProdukResponse>, response: Response<ProdukResponse>) {
                produkItem = response.body()?.produk!!
                val dataItem = ItemCategoryAdapter(produkItem, { dataItem, posision ->
                    dataProdukItem = dataItem
                    posisi = posision
                    openDialog()
                })
                rv_item_category.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = dataItem
                }
            }
        })
    }

    fun openDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.input_item_dialog)
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams().apply {
            copyFrom(dialog.window?.attributes)
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }
        var nama: String = "nama"
        val tambah = dialog.findViewById<View>(R.id.bt_add_dialog) as Button
        val sub = dialog.findViewById<View>(R.id.bt_subtreact_dialog) as Button
        val insert = dialog.findViewById<View>(R.id.bt_tambah) as Button
        val dimis = dialog.findViewById<View>(R.id.dimis_dialog) as ImageButton
        val qty = dialog.findViewById<View>(R.id.et_quantity) as EditText
        val note_saller = dialog.findViewById<View>(R.id.et_note_for_seller) as EditText

        tambah.setOnClickListener {
            qty.setText("${count++}")

        }

        sub.setOnClickListener {
            qty.setText("${qty.text.toString().toInt() - 1}")
        }

        insert.setOnClickListener {
            toast("berhasil di tambahkan")
            dialog.dismiss()
        }

        dimis.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.getWindow()?.setAttributes(lp)
    }

    private fun openDrawer() {
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.nav_draw_pop_up, null)
        val popupWindow = PopupWindow(
            view,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        popupWindow.setAnimationStyle(R.style.popup_window_animation)

        val closeButton = view.findViewById<ImageButton>(R.id.ib_close)
        val buttonHome = view.findViewById<CardView>(R.id.button_home)
        val buttonChart = view.findViewById<CardView>(R.id.button_chart)
        val buttonTransaction = view.findViewById<CardView>(R.id.button_transaction)
        val buttonAccount = view.findViewById<CardView>(R.id.button_account)
        val buttonLogOut = view.findViewById<CardView>(R.id.button_logout)

        buttonHome.setOnClickListener { intent(MenuActivity::class.java) }
        buttonChart.setOnClickListener { intent(ChartActivity::class.java) }
        buttonTransaction.setOnClickListener { intent(TransactionActivity::class.java) }
        buttonAccount.setOnClickListener { intent(AkunActivity::class.java) }
        buttonLogOut.setOnClickListener { dialogLogout() }

        closeButton.setOnClickListener { popupWindow.dismiss() }

        TransitionManager.beginDelayedTransition(root_layout)
        popupWindow.showAtLocation(root_layout, Gravity.START, 0, 0)

    }

    private fun dialogLogout() {
        logOut.setNegativeButton("NO") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("YES") { prefer, _ ->
                pref.logout()
                intent(MainActivity::class.java)
                finish()
            }.setTitle("Logout")
            .setMessage("Apakah Anda Ingin Logout ?").create()
        logOut.show()
    }


    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        toast("Please Press Back Again to close apps")

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }


}

