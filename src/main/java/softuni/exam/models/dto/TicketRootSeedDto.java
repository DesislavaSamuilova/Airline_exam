package softuni.exam.models.dto;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "tickets")
@XmlAccessorType(XmlAccessType.FIELD)
public class TicketRootSeedDto {

    @XmlElement(name = "ticket")
    private List<TicketSeedDto> ticket;

    public List<TicketSeedDto> getTicket() {
        return ticket;
    }

    public void setTicket(List<TicketSeedDto> ticket) {
        this.ticket = ticket;
    }
}