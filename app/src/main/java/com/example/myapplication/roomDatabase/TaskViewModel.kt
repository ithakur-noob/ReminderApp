import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Task
import com.example.myapplication.roomDatabase.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {

    private val repository: TaskRepository = TaskRepository()

    fun insertTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(task)
        }
    }
    suspend fun doesDueDateExist(dueDate: Long): Boolean {
        return repository.doesDueDateExist(dueDate)
    }

    fun getAllTasks() = repository.getAllTasks()
}
