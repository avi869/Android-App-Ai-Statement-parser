import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aistatementparser.Model.TransactionDto
import com.example.aistatementparser.StatementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class StatementViewModel(
    private val repository: StatementRepository
) : ViewModel() {

    companion object {
        private const val TAG = "StatementVM"
    }

    // 1️⃣ Full transaction list (single source of truth)
    private val _allTransactions = MutableStateFlow<List<TransactionDto>>(emptyList())
    val allTransactions: StateFlow<List<TransactionDto>> = _allTransactions.asStateFlow()


    // 2️⃣ Selected category (null = show all)
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()


    // 3️⃣ Loading + Error
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()


    // 4️⃣ 🔥 REACTIVE FILTER PIPELINE
    val filteredTransactions: StateFlow<List<TransactionDto>> =
        combine(
            allTransactions,
            selectedCategory
        ) { transactions, category ->

            if (category.isNullOrBlank()) {
                transactions
            } else {
                transactions.filter {
                    it.Category.equals(category, ignoreCase = true)
                }
            }
        }
            .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5_000), initialValue = emptyList())

    // 5️⃣ Upload PDF
    fun uploadPdf(part: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val result = repository.uploadPdf(part)
                _allTransactions.value = result
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // 6️⃣ Called when user clicks category
    fun setSelectedCategory(category: String?) {
        Log.d(TAG, "Category selected: $category")
        _selectedCategory.value = category
    }

    /* ---- existing uriToFile() and fileToMultipart() stay same ---- */
}
