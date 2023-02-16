package timeWizard;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name="table_tasks")
public class TableTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="text")
    private String text;
    @Column(name="column_number")
    private int ColumnNumber;
    @Column(name="user_email")
    private String email;
}
