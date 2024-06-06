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

        setupRecyclerView()

        binding.buttonAddProject.setOnClickListener {
            showAddProjectDialog()
        }

        fetchProjects()
    }

    private fun setupRecyclerView() {
        projectAdapter = ProjectAdapter()
        binding.recyclerViewProjects.apply {
            layoutManager = LinearLayoutManager(this@AdminActivity)
            adapter = projectAdapter
        }

        // Defina o ouvinte de clique passando uma instância da interface OnItemClickListener
        projectAdapter.setOnItemClickListener(object : ProjectAdapter.OnItemClickListener {
            override fun onItemClick(project: ProjectResponse) {
                // Aqui você pode lidar com o evento de clique do item
                showEditProjectDialog(project)
            }
        })

        // Defina o ouvinte de clique longo passando uma instância da interface OnItemClickListener
        projectAdapter.setOnItemLongClickListener(object : ProjectAdapter.OnItemLongClickListener {
            override fun onItemLongClick(project: ProjectResponse): Boolean {
                // Aqui você pode lidar com o evento de clique longo do item
                removeProject(project)
                return true
            }
        })
    }




    private fun showAddProjectDialog() {
        val inputName = EditText(this)
        val inputStartDate = EditText(this)
        val inputEndDate = EditText(this)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Project")
            .setView(inputName)
            .setPositiveButton("Add") { _, _ ->
                val name = inputName.text.toString()
                val startDate = inputStartDate.text.toString()
                val endDate = inputEndDate.text.toString()
                addProject(name, startDate, endDate)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()

        dialog.show()
    }

    private fun showEditProjectDialog(project: ProjectResponse) {
        val inputName = EditText(this)
        val inputStartDate = EditText(this)
        val inputEndDate = EditText(this)

        inputName.setText(project.nameproject)
        inputStartDate.setText(project.startdatep)
        inputEndDate.setText(project.enddatep)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Project")
            .setView(inputName)
            .setPositiveButton("Save") { _, _ ->
                val name = inputName.text.toString()
                val startDate = inputStartDate.text.toString()
                val endDate = inputEndDate.text.toString()
                editProject(project, name, startDate, endDate)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()

        dialog.show()
    }

    private fun addProject(name: String, startDate: String, endDate: String) {
        val projectRequest = ProjectRequest(
            name,
            startDate,
            endDate,
            idstate = 1, // Defina o estado conforme necessário
            iduser = RetrofitClient.getUserId() ?: -1, // Obtenha o ID do usuário autenticado
            completionstatus = false, // Defina conforme necessário
            performancereview = null, // Defina conforme necessário
            obs = null // Defina conforme necessário
        )

        apiService.createProject(projectRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showToast("Project added successfully")
                    fetchProjects() // Atualize a lista de projetos após a adição bem-sucedida
                } else {
                    showToast("Failed to add project")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun editProject(project: ProjectResponse, name: String, startDate: String, endDate: String) {
        val projectRequest = ProjectRequest(
            name,
            startDate,
            endDate,
            project.idstate,
            project.iduser,
            project.completionstatus,
            project.performancereview,
            project.obs
        )

        apiService.updateProject(project.idproject, projectRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showToast("Project updated successfully")
                    fetchProjects() // Atualize a lista de projetos após a edição bem-sucedida
                } else {
                    showToast("Failed to update project")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun removeProject(project: ProjectResponse) {
        apiService.deleteProject(project.idproject).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showToast("Project removed successfully")
                    fetchProjects() // Atualize a lista de projetos após a remoção bem-sucedida
                } else {
                    showToast("Failed to remove project")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
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
