package mx.edu.ittepic.tpdm_u3_practica1_15400998

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var descripcion : EditText ?= null
    var monto : EditText ?= null
    var fechaVencimiento : EditText ?= null
    var pagado : EditText ?= null
    var insertar : Button ?= null
    var lis : ListView ?= null

    var baseRemotaP3 = FirebaseFirestore.getInstance()
    var registrosRemotosP3 = ArrayList<String>()
    var keysP3 = java.util.ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        descripcion = findViewById(R.id.descripcion)
        monto = findViewById(R.id.monto)
        fechaVencimiento = findViewById(R.id.fechaVencimiento)
        pagado = findViewById(R.id.pagado)
        insertar = findViewById(R.id.insertar)
        lis = findViewById(R.id.list)

        insertar?.setOnClickListener{
            //<metodo para crear la coleccion y el primer documento por primera vez, despues sera solo para insertar documentos en la BD>
            var insertar = hashMapOf(
                "descripcion" to descripcion?.text.toString(),
                "monto" to monto?.text.toString().toDouble(),
                "fechaVencimiento" to fechaVencimiento?.text.toString(),
                "pagado" to pagado?.text.toString()
            )
            baseRemotaP3.collection("recibos")
                .add(insertar as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this,"Se inserto correctamente",Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Error!. No se inserto"+it.message,Toast.LENGTH_SHORT)
                        .show()
                }
            limpiarCampos()
        }
        //</metodo para crear la coleccion y el primer documento por primera vez, despues sera solo para insertar documentos en la BD>

        //<metodo para mostrar registros de la BD>
        baseRemotaP3.collection("recibos")
            .addSnapshotListener { querySnapshot, e ->
                if(e != null){
                    Toast.makeText(this,"Error!. No se pudo hacer consulta",Toast.LENGTH_SHORT)
                        .show()
                    return@addSnapshotListener
                }
                registrosRemotosP3.clear()
                keysP3.clear()
                for(document in querySnapshot!!){
                    var con = "Descripción: ${document.getString("descripcion")}\n" +
                            "Monto: ${document.getDouble("monto")}\n" +
                            "Fecha de vencimiento: ${document.getString("fechaVencimiento")}\n" +
                            "Pagado: ${document.getString("pagado")}"
                        /*document.getString("descripcion")+"   "+
                            document.getDouble("monto")+"   "+
                            document.getString("fechaVencimiento")+"   "+
                            document.getString("pagado")*/
                    registrosRemotosP3.add(con)
                    keysP3.add(document.id)
                }
                if(registrosRemotosP3.size==0){
                    registrosRemotosP3.add("No hay datos aun para mostrar")
                }
                var adaptador = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,registrosRemotosP3)
                list?.adapter = adaptador
            }
        //</metodo para mostrar registros de la BD>

        list?.setOnItemClickListener { adapterView, view, position, l ->
            if(keysP3.size==0){
                return@setOnItemClickListener
            }
            else{
                AlertDialog.Builder(this)
                    .setTitle("!Atención")
                    .setMessage("¿Qué desea hacer con ${registrosRemotosP3.get(position)} ?")
                    .setPositiveButton("Eliminar"){ dialogInterface, i ->  
                        baseRemotaP3.collection("recibos")
                            .document(keysP3.get(position))
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this,"Eliminación correcta!",Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this,"Eliminación erronea",Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                    .setNegativeButton("Actualizar"){dialogInterface, i ->
                        var neWind = Intent(this,Main2Activity::class.java)
                        neWind.putExtra("id",keysP3.get(position))
                        startActivity(neWind)
                    }
                    .setNeutralButton("Cancelar"){ dialogInterface, i ->
                    }.show()
            }
        }
    }
    fun limpiarCampos(){
        descripcion?.setText("")
        monto?.setText("")
        fechaVencimiento?.setText("")
        pagado?.setText("")
    }
}
