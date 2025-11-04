import java.sql.*;
/**
 * Jdbc1
 */
public class Jdbc1 {

    public static void main(String[] args) throws Exception{
        String url="jdbc:mysql://localhost:3306/giraffe";
        String name="mysql";
        String pwd="root";
        String query="select name from student where student_id=3";
        Class.forName("mysql.jdbc.Driver");
        Connection con=DriverManager.getConnection(url,name,pwd);
        Statement st=con.createStatement();
        ResultSet rs=st.executeQuery(query);
        rs.next();
        String myname=rs.getString("name");
        System.out.println(myname);
        st.close();
        con.close();
    }
}