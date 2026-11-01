import Form.FormLogin;

/**
 * Main Class untuk menjalankan aplikasi Bi-Kantin.
 * Menampilkan FormLogin saat aplikasi pertama kali dijalankan.
 */
public class Main {

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            // Abaikan jika Nimbus tidak tersedia
        }

        java.awt.EventQueue.invokeLater(() -> {
            new FormLogin().setVisible(true);
        });
    }
}
