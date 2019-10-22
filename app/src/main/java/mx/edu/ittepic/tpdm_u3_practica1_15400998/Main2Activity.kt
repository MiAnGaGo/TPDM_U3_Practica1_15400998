package mx.edu.ittepic.tpdm_u3_practica1_15400998

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class Main2Activity : AppCompatActivity() {
    var descripcion2 : EditText ?= null
    var monto2 : EditText ?= null
    var fechaVencimiento2 : EditText ?= null
    var pagado2 : EditText ?= null
    var actualizar : Button ?= null
    var regresar : Button ?= null

    //objeto firestore
    var baseRemota3P = FirebaseFirestore.getInstance()
    var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        descripcion2 = findViewById(R.id.descripcion2)
        monto2 = findViewById(R.id.monto2)
        fechaVencimiento2 = findViewById(R.id.fechaVencimiento2)
        pagado2 = findViewById(R.id.pagado2)
        actualizar = findViewById(R.id.actualizar)
        regresar = findViewById(R.id.regresar)

        id = intent.extras?.getString("id")!!

        //<metodo para llenar los campos con el id que viene de la otra interfaz>
        baseRemota3P.collection("recibos")
            .document(id)
            .get()
            .addOnSuccessListener {
                descripcion2?.setText(it.getString("descripcion"))
                monto2?.setText(it.getDouble("monto").toString())
                fechaVencimiento2?.setText(it.getString("fechaVencimiento"))
                pagado2?.setText(it.getString("pagado"))
            }
            .addOnFailureListener {
                descripcion2?.setText("No datos")
                monto2?.setText("No datos")
                fechaVencimiento2?.setText("No datos")
                pagado2?.setText("No datos")

                descripcion2?.isEnabled = false
                monto2?.isEnabled = false
                fechaVencimiento2?.isEnabled = false
                pagado2?.isEnabled = false
            }
        //</metodo para llenar los campos con el id que viene de la otra interfaz>

        actualizar?.setOnClickListener {
            //<metodo para crear la coleccion y despues actualizar documentos en la BD>
            var act = hashMapOf(
                "descripcion" to descripcion2?.text.toString(),
                "monto" to monto2?.text.toString().toDouble(),
                "fechaVencimiento" to fechaVencimiento2?.text.toString(),
                "pagado" to pagado2?.text.toString()
            )
            baseRemota3P.collection("recibos")
                .document(id)
                .set(act as Map<String, Any>)
                .addOnSuccessListener {
                    limpiarCampos()
                    Toast.makeText(this,"Se actualizo correctamente",Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Error!. No se actualizo"+it.message,Toast.LENGTH_SHORT)
                        .show()
                }
            //</metodo para crear la coleccion y despues actualizar documentos en la BD>
        }

        regresar?.setOnClickListener { finish() }
    }
    fun limpiarCampos(){
        descripcion2?.setText("")
        monto2?.setText("")
        fechaVencimiento2?.setText("")
        pagado2?.setText("")
    }
}
