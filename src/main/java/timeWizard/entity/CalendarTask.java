package timeWizard.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name="calendar_tasks")
public class CalendarTask {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String title;
	private String definition;
	@Column(name="user_email")
	private String email;
	
	@Column(name="start_date")
	private Date startDate;
	@Column(name="end_date")
	private Date endDate;
	
	private Boolean completed;

	@Column(name="color")
	private String colorInHex;

	@Column(name="frequency")
	private int frequency;
	@Column(name="time_unit")
	private String timeUnit;
	
	public CalendarTask(String title, String definition, String email, Date startDate, Date endDate) {
		
		this.title = title;
		this.definition = definition;
		this.email = email;
		this.startDate = startDate;
		this.endDate = endDate;

	}
	
	
	
}
