package com.traductor.traductorlsc.BaseDeDatos;

public class Utilidades {
    //Constantes campos tabla Vocabulario
    public static final String NOMBRE_BASEDEDATOS = "bd_vocabulario";
    public static final String TABLA_VOCABULARIO = "vocabulario";
    public static final String CAMPO_ID = "id";
    public static final String CAMPO_PALABRA = "palabra";
    public static final String CAMPO_RUTA = "ruta";
    public static final String CAMPO_CATEGORIA = "categoria";
    public static final int VERSION_BASEDEDATOS = 1;

    //Creacion de la tabla en la BD
    public static final String sqlCreate = "CREATE TABLE IF NOT EXIST " + TABLA_VOCABULARIO + " ("
            + CAMPO_ID + " INTEGER PRIMARY KEY,"
            + CAMPO_PALABRA + " TEXT, "
            + CAMPO_RUTA + " TEXT, "
            + CAMPO_CATEGORIA + " TEXT)";

    //Eliminación de la base de datos en caso de que exista
    public static final String sqlDrop = "DROP TABLE IF EXISTS " + TABLA_VOCABULARIO;

    //Insersión de datos en la BD
    public static final String sqlInsert = "INSERT INTO "+TABLA_VOCABULARIO+" ("+CAMPO_ID+", "+CAMPO_PALABRA+", "+CAMPO_CATEGORIA+", "+CAMPO_RUTA+")"
            +"VALUES"
            +"(001, 'a', 'ABECEDARIO', 'R.raw.a'),"
            +"(002, 'abajo', 'NOCIONES', 'R.raw.abajo'),"
            +"(003, 'abril', 'MESES', 'R.raw.abril'),"
            +"(004, 'abuela',	'FAMILIA', 'R.raw.abuela'),"
            +"(005, 'abuelo', 'FAMILIA', 'R.raw.abuelo'),"
            +"(006, 'aburrido', 'SENTIMIENTOS', 'R.raw.aburrido'),"
            +"(007, 'adelante', 'NOCIONES', 'R.raw.adelante'),"
            +"(008, 'adios', 'SALUDOS', 'R.raw.adios'),"
            +"(009, 'aeropuerto', 'LUGARES' 'R.raw.aeropuerto'),"
            +"(010, 'agosto', 'TEMPORALIDAD', 'R.raw.agosto')";

}
