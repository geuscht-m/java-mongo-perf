import org.springframework.data.annotation.Id;

public class SmallDoc {
    @Id
    public String id;

    public String frontSpring;
    public String rearSpring;
    public double ratio;

    public SmallDoc() {
    }

    public SmallDoc(String front, String back, double r) {
	frontSpring = front;
	rearSpring  = back;
	ratio       = r;
    }
}
