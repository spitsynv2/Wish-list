
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prm_projekt_2_s22599.databinding.ProductImageBinding
import java.io.File

class ProductImageViewHolder(val binding: ProductImageBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(imagePath: String, isSelected: Boolean) {
        val bitmap = BitmapFactory.decodeFile(imagePath)
        binding.imageProduct.setImageBitmap(bitmap)
        binding.selectedFrame.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE
    }
}

class ProductImageAdapter : RecyclerView.Adapter<ProductImageViewHolder>() {

    private val images = mutableListOf<String>()
    private var selectedPosition: Int = 0
    val selectedImagePath: String
        get() = if (images.isNotEmpty()) images[selectedPosition] else ""

    internal fun loadImages() {
        val folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
        val folder = File(folderPath)
        if (folder.exists() && folder.isDirectory) {
            images.clear()
            folder.listFiles()?.forEach { file ->
                if (file.isFile) {
                    images.add(file.absolutePath)
                    images.reverse()
                }
            }
            notifyDataSetChanged()
        } else {
            Log.e("ProductImageAdapter", "Folder does not exist or is not a directory")
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImageViewHolder {
        val binding = ProductImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductImageViewHolder(binding).also { vh ->
            binding.root.setOnClickListener {
                setSelected(vh.layoutPosition)
            }
        }
    }

    private fun setSelected(layoutPosition: Int) {
        notifyItemChanged(selectedPosition)
        selectedPosition = layoutPosition
        notifyItemChanged(selectedPosition)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ProductImageViewHolder, position: Int) {
        holder.bind(images[position], position == selectedPosition)
    }

    fun setSelection(imagePath: String?) {
        val index = images.indexOfFirst { it == imagePath }
        if (index == -1) return
        setSelected(index)
    }
}
