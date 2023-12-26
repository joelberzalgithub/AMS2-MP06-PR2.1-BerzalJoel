package com.project;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static Scanner in = new Scanner(System.in); // System.in és global

    public static void main(String[] args) throws SQLException {

        String basePath = System.getProperty("user.dir") + "/data/";
        String filePath = basePath + "database.db";
        ResultSet rs = null;

        // Si no hi ha l'arxiu creat, el crea i li posa dades
        File fDatabase = new File(filePath);
        if (!fDatabase.exists()) { initDatabase(filePath); }

        // Connectar (crea la BBDD si no existeix)
        Connection conn = UtilsSQLite.connect(filePath);

        // Llistar les taules
        ArrayList<String> taules = UtilsSQLite.listTables(conn);
        System.out.println(taules);

        boolean running = true;
		
		while (running) {
			
			System.out.println("Escull una opcio:\n\n0) Mostrar taula Faccio\n1) Mostrar taula Personatge\n2) Mostrar personatges per faccio\n3) Mostrar el millor atacant per faccio\n4)Mostrar el millor defensor per faccio\n100) Sortir\n");
			
			try {
				
				int opcio = Integer.valueOf(llegirLinia("Opcio: "));
				
				switch (opcio) {
					case 0: mostrarTaulaFaccion(conn, rs);
							break;
					case 1: mostrarTaulaPersonatge(conn, rs);
							break;
					case 2: mostrarPersonatges(conn, rs, 1);
                            mostrarPersonatges(conn, rs, 2);
                            mostrarPersonatges(conn, rs, 3);
							break;
                    case 3: mostrarPersonatgesOrdenats(conn, rs, "atac");
							break;
                    case 4: mostrarPersonatgesOrdenats(conn, rs, "defensa");
							break;
					case 100: running = false;
							break;
					default: System.out.println("\nOpcio fora del rang!");
							break;
				}
				
				System.out.println("\n*****************************************************************************************************************************************************************************\n");
			
			} catch (Exception e) {
				System.out.println("\nOpcio no numerica!\n\n*****************************************************************************************************************************************************************************\n");
			}
		}

        // Desconnectar
        UtilsSQLite.disconnect(conn);
    }

    static String llegirLinia (String text) {
		System.out.print(text);
		return in.nextLine();
    }

    static void mostrarTaulaFaccion(Connection conn, ResultSet rs) {
        try {
            rs = UtilsSQLite.querySelect(conn, "SELECT * FROM faccio;");
            while (rs.next()) {
                System.out.println("\nId: " + rs.getInt("id")
                                 + "; Nom: " + rs.getString("nom")
                                 + "; Resum: " + rs.getString("resum"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    static void mostrarTaulaPersonatge(Connection conn, ResultSet rs) {
        try {
            rs = UtilsSQLite.querySelect(conn, "SELECT * FROM personatge;");
            System.out.println("");
            while (rs.next()) {
                System.out.println("Id: " + rs.getInt("id")
                                 + "; Nom: " + rs.getString("nom")
                                 + "; Atac: " + rs.getString("atac")
                                 + "; Defensa: " + rs.getString("defensa")
                                 + "; Id Faccio: " + rs.getString("idFaccio"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    static void mostrarPersonatges(Connection conn, ResultSet rs, int idFaccio) {
        try {
            rs = UtilsSQLite.querySelect(conn, "SELECT * FROM personatge WHERE idFaccio = " + idFaccio + ";");
            if (idFaccio == 1) { System.out.println("\nPersonatges de la faccio Cavallers:\n"); }
            else if (idFaccio == 2) { System.out.println("\nPersonatges de la faccio Vikings:\n"); }
            else { System.out.println("\nPersonatges de la faccio Samurais:\n"); }
            while (rs.next()) {
                System.out.println("Id: " + rs.getInt("id")
                                 + "; Nom: " + rs.getString("nom")
                                 + "; Atac: " + rs.getString("atac")
                                 + "; Defensa: " + rs.getString("defensa")
                                 + "; Id Faccio: " + rs.getString("idFaccio"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    static void mostrarPersonatgesOrdenats(Connection conn, ResultSet rs, String ordre) {
        try {
            rs = UtilsSQLite.querySelect(conn, "SELECT * FROM personatge ORDER BY " + ordre + " DESC LIMIT 1;");
            while (rs.next()) {
                System.out.println("\nId: " + rs.getInt("id")
                                 + "; Nom: " + rs.getString("nom")
                                 + "; Atac: " + rs.getString("atac")
                                 + "; Defensa: " + rs.getString("defensa")
                                 + "; Id Faccio: " + rs.getString("idFaccio"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    static void initDatabase (String filePath) {
        
        // Connectar (crea la BBDD si no existeix)

        Connection conn = UtilsSQLite.connect(filePath);

        // Esborrar la taula Facció (per si existeix)

        UtilsSQLite.queryUpdate(conn, "DROP TABLE IF EXISTS faccio;");

        // Crear una nova taula Facció

        UtilsSQLite.queryUpdate(conn, "CREATE TABLE IF NOT EXISTS faccio ("
                                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                                    + "nom VARCHAR(15) NOT NULL,"
                                    + "resum VARCHAR(500) NOT NULL);");
        
        // Esborrar la taula Personatge (per si existeix)

        UtilsSQLite.queryUpdate(conn, "DROP TABLE IF EXISTS personatge;");

        // Crear una nova taula Personatge
        
        UtilsSQLite.queryUpdate(conn, "CREATE TABLE IF NOT EXISTS personatge ("
                                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                                    + "nom VARCHAR(15) NOT NULL,"
                                    + "atac FLOAT NOT NULL,"
                                    + "defensa FLOAT NOT NULL,"
                                    + "idFaccio INT FOREING KEY);");
        
        // Afegir 3 faccions diferents

        UtilsSQLite.queryUpdate(conn, "INSERT INTO faccio (nom, resum) VALUES (\"Cavallers\", \"Els Cavallers son una de les quatre faccions jugables de For Honor. La seva creença es que moltes, si no totes, les ruines antigues van ser construides pels seus avantpassats. Els Cavallers havien estat dispersos durant segles pero han comencat a reunir-se sota una unica bandera, la de la Legio de Ferro. Encara hi ha qui, pero, opta per reunir la seva propia Legio, i la seva aliança amb la Legio de Ferro es inestable en el millor dels casos.\");");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO faccio (nom, resum) VALUES (\"Vikings\", \"Els vikings son una de les quatre faccions jugables de For Honor. Abans es pensava que havien desaparegut, els vikings han tornat, en gran nombre, de l'altra banda del mar. Han vingut per prendre noves terres, saquejar, expandir els clans i recuperar la seva antiga patria al nord. Centenars de clans dominen ara enmig de la tundra freda i gelada.\");");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO faccio (nom, resum) VALUES (\"Samurais\", \"El Samurai de l'Imperi de l'Alba es una de les cinc faccions jugables de For Honor. Aquestes persones provenen d'una terra, llunyana a l'est i expliquen la historia d'una patria i d'un emperador que es van perdre al mar i al foc durant el gran cataclisme. Ara, mes d'un mil·lennis despres, els guerrers nomades ja no vaguen i s'han reconstruit en una nova terra amb un nou emperador.\");");
        
        // Afegir 3 personatges per cada facció

        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (nom, atac, defensa, idFaccio) VALUES (\"Warden\", 130, 120, 1);");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (nom, atac, defensa, idFaccio) VALUES (\"Conqueror\", 140, 120, 1);");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (nom, atac, defensa, idFaccio) VALUES (\"Pacekeeper\", 120, 120, 1);");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (nom, atac, defensa, idFaccio) VALUES (\"Highlander\", 125, 120, 2);");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (nom, atac, defensa, idFaccio) VALUES (\"Shaman\", 120, 120, 2);");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (nom, atac, defensa, idFaccio) VALUES (\"Jormungandr\", 130, 140, 2);");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (nom, atac, defensa, idFaccio) VALUES (\"Shinobi\", 120, 135, 3);");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (nom, atac, defensa, idFaccio) VALUES (\"Hitokiri\", 140, 130, 3);");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO personatge (nom, atac, defensa, idFaccio) VALUES (\"Kyoshin\", 120, 120, 3);");
        
        // Desconnectar
        UtilsSQLite.disconnect(conn);
    }
}