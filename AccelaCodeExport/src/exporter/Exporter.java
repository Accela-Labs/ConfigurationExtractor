package exporter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import Util.Enviroments;

public interface Exporter {
	void export(String reposlocation, Connection con, Enviroments e, String agency, String repoadd) throws IOException, SQLException;
}
