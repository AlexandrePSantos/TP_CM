import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trabpratico.databinding.ActivityAdminBinding
import com.example.trabpratico.network.ApiService
import com.example.trabpratico.network.ProjectRequest
import com.example.trabpratico.network.ProjectResponse
import com.example.trabpratico.ui.adapters.ProjectAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var apiService: ApiService
    private lateinit var projectAdapter: ProjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = RetrofitClient.instance


        fetchProjects()
    }



    private fun fetchProjects() {
        apiService.getAllProjects().enqueue(object : Callback<List<ProjectResponse>> {
            override fun onResponse(call: Call<List<ProjectResponse>>, response: Response<List<ProjectResponse>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        projectAdapter.submitList(it)
                    }
                } else {
                    showToast("Failed to load projects")
                }
            }

            override fun onFailure(call: Call<List<ProjectResponse>>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}