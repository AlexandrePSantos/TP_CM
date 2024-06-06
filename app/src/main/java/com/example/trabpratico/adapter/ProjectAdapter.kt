import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trabpratico.databinding.ItemProjectBinding
import com.example.trabpratico.network.ProjectResponse

class ProjectAdapter : ListAdapter<ProjectResponse, ProjectAdapter.ProjectViewHolder>(DiffCallback()) {

    interface OnItemClickListener {
        fun onItemClick(project: ProjectResponse)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(project: ProjectResponse): Boolean
    }

    private var listener: OnItemClickListener? = null
    private var longClickListener: OnItemLongClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setOnItemLongClickListener(longClickListener: OnItemLongClickListener) {
        this.longClickListener = longClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = ItemProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)

        holder.itemView.setOnClickListener {
            listener?.onItemClick(currentItem)
        }

        holder.itemView.setOnLongClickListener {
            longClickListener?.onItemLongClick(currentItem) ?: false
        }
    }

    class ProjectViewHolder(private val binding: ItemProjectBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(project: ProjectResponse) {
            binding.textViewProjectName.text = project.nameproject
            binding.textViewStartDate.text = project.startdatep
            binding.textViewEndDate.text = project.enddatep
            binding.textViewState.text = project.idstate.toString() // Consider converting state ID to state name if necessary
            binding.textViewCompletionStatus.text = if (project.completionstatus) "Completed" else "Incomplete"
            binding.textViewPerformanceReview.text = project.performancereview ?: "No review"
            binding.textViewObs.text = project.obs ?: "No observations"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ProjectResponse>() {
        override fun areItemsTheSame(oldItem: ProjectResponse, newItem: ProjectResponse): Boolean {
            return oldItem.idproject == newItem.idproject
        }

        override fun areContentsTheSame(oldItem: ProjectResponse, newItem: ProjectResponse): Boolean {
            return oldItem == newItem
        }
    }
}
