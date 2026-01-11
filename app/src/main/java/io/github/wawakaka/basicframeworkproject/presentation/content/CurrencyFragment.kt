package io.github.wawakaka.basicframeworkproject.presentation.content

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.wawakaka.basicframeworkproject.R
import io.github.wawakaka.basicframeworkproject.base.BaseFragment
import io.github.wawakaka.basicframeworkproject.databinding.FragmentCurrencyBinding
import io.github.wawakaka.basicframeworkproject.presentation.content.adapter.CurrencyListAdapter
import io.github.wawakaka.basicframeworkproject.utilities.makeInvisible
import io.github.wawakaka.basicframeworkproject.utilities.makeVisible
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.core.scope.Scope

class CurrencyFragment : BaseFragment(), CurrencyContract.View {

    private var _binding: FragmentCurrencyBinding? = null
    private val binding get() = _binding!!

    private var scope: Scope? = null
    private lateinit var presenter: CurrencyPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Koin presenter
        presenter = getKoin().get<CurrencyPresenter>()

        presenter.attach(this)
        init()
    }

    override fun onDestroyView() {
        presenter.detach()
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        scope?.close()
        super.onDestroy()
    }

    override fun showLoading() {
        binding.progressLoading.makeVisible()
    }

    override fun hideLoading() {
        binding.progressLoading.makeInvisible()
    }

    override fun onGetDataSuccess(data: List<Pair<String, Double>>) {
        binding.recyclerCurrency.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CurrencyListAdapter(data).apply {
                this.clickListener = {
                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
                }
            }
        }
        Log.d(TAG, "onGetCurrentWeather next")
    }

    override fun onGetDataFailed(throwable: Throwable) {
        Toast.makeText(activity, throwable.toString(), Toast.LENGTH_LONG).show()
        Log.e(TAG, "error onGetCurrentWeather", throwable)
    }

    private fun init() {
        initGoButton()
        initProgressbar()
    }

    private fun initGoButton() {
        binding.buttonGo.setOnClickListener {
            lifecycleScope.launch {
                presenter.onButtonClickedEvent()
            }
        }
    }

    private fun initProgressbar() {
        context?.let {
            binding.progressLoading.indeterminateDrawable.setColorFilter(
                ContextCompat.getColor(
                    it,
                    R.color.colorPrimary
                ), PorterDuff.Mode.SRC_ATOP
            )
        }
    }

    companion object {
        private val TAG = CurrencyFragment::class.java.simpleName
    }
}
