package solution;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
@Path("/organizer")
public class SignUpSheetDao {
	EntityManagerFactory factory = Persistence.createEntityManagerFactory("SignUpSheet");
	EntityManager em = null;
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/")
	public void register(Organizer organizer) {
		em = factory.createEntityManager();
		em.getTransaction().begin();
		
		em.persist(organizer);
		
		em.getTransaction().commit();
		em.close();
	}
	public boolean validate(int organizer_id) {
		em = factory.createEntityManager();
		em.getTransaction().begin();

		Organizer organizer = em.find(Organizer.class, organizer_id);

		em.getTransaction().commit();
		em.close();

		if(organizer == null)
			return false;
		return true;
	}
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/sheet")
	public List<Sheet> createSheet(@PathParam("id") int organizer_id, Sheet sheet) {
		em = factory.createEntityManager();
		em.getTransaction().begin();

		Organizer organizer = em.find(Organizer.class, organizer_id);
		organizer.getSheets().add(sheet);
		sheet.setOrganizer(organizer);
		em.merge(organizer);
		List<Sheet> sheets = organizer.getSheets();

		em.getTransaction().commit();
		em.close();
		
		return sheets;
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/sheet")
	public List<Sheet> getAllSheets(@PathParam("id") int organizer_id) {
		em = factory.createEntityManager();
		em.getTransaction().begin();

		Organizer organizer = em.find(Organizer.class, organizer_id);
		List<Sheet> sheet = organizer.getSheets();

		em.getTransaction().commit();
		em.close();
		
		return sheet;
	}
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/sheet/{id}/slot")
	public List<Slot> addTimeSlot(@PathParam("id") int sheet_id, Slot slot) {
		em = factory.createEntityManager();
		em.getTransaction().begin();

		Sheet sheet = em.find(Sheet.class, sheet_id);
		sheet.getTimeSlots().add(slot);
		List<Slot> slots = sheet.getTimeSlots();
		slot.setSheet(sheet);
		em.merge(sheet);

		em.getTransaction().commit();
		em.close();
		
		return slots;
	}
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/sheet/{sheetId}/slot/{slotId}")
	public List<Slot> deleteTimeSlot(@PathParam("sheetId") int sheet_id, @PathParam("slotId") int slot_id) {
		em = factory.createEntityManager();
		em.getTransaction().begin();

		Slot slot = em.find(Slot.class, slot_id);
		em.remove(slot);
		
		Sheet sheet = em.find(Sheet.class, sheet_id);		
		List<Slot> slots = sheet.getTimeSlots();

		em.getTransaction().commit();
		em.close();
		
		return slots;
	}
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/sheet/{sheetId}/slot/{slotId}")
	public List<Slot> updateTimeSlot(@PathParam("sheetId") int sheet_id,@PathParam("slotId")  int slot_id, Slot newSlot) {
		em = factory.createEntityManager();
		em.getTransaction().begin();

		Slot slot = em.find(Slot.class, slot_id);
		slot.setNotes(newSlot.getNotes());
		slot.setSlotDate(newSlot.getSlotDate());
		slot.setWho(slot.getWho());
		em.merge(slot);
		
		Sheet sheet = em.find(Sheet.class, sheet_id);		
		List<Slot> slots = sheet.getTimeSlots();

		em.getTransaction().commit();
		em.close();
		
		return slots;
	}
	public void exportToXml(String xmlFileName, Organizer organizer) {
		File file = new File(xmlFileName);
		try {
			JAXBContext jaxb = JAXBContext.newInstance(Organizer.class);
			Marshaller marshaller = jaxb.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(organizer, file);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void exportOrganizersToXml(String xmlFileName, Organizers organizers) {
		File file = new File(xmlFileName);
		try {
			JAXBContext jaxb = JAXBContext.newInstance(Organizers.class);
			Marshaller marshaller = jaxb.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(organizers, file);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Organizers importOrganizersFromXml(String xmlFileName) {
		File file = new File(xmlFileName);
		Organizers orgs = new Organizers();
		try {
			JAXBContext jaxb = JAXBContext.newInstance(Organizers.class);
			Unmarshaller marshaller = jaxb.createUnmarshaller();
			orgs = (Organizers) marshaller.unmarshal(file);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return orgs;
	}
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	// <a href="rest/organizer">Get All Organizers</a>
	public Organizer getOrganizer(@PathParam("id") int organizer_id) {
		em = factory.createEntityManager();
		em.getTransaction().begin();

		Organizer organizer = em.find(Organizer.class, organizer_id);

		em.getTransaction().commit();
		em.close();
		
		return organizer;
	}
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	// <a href="rest/organizer">Get All Organizers</a>
	public List<Organizer> getAllOrganizers() {
		em = factory.createEntityManager();
		em.getTransaction().begin();

		List<Organizer> organizers = em.createNamedQuery("findAllOrganizers").getResultList();

		em.getTransaction().commit();
		em.close();
		
		return organizers;
	}
	public void transform(String inputFileName, String outputFileName, String xsltFileName) {
		File inputFile = new File(inputFileName);
		File outputFile = new File(outputFileName);
		File xsltFile = new File(xsltFileName);
		javax.xml.transform.stream.StreamSource inputSrc = new StreamSource(inputFile);
		javax.xml.transform.stream.StreamSource xsltSrc = new StreamSource(xsltFile);
		javax.xml.transform.stream.StreamResult outputSrc = new StreamResult(outputFile);
		javax.xml.transform.TransformerFactory factory = TransformerFactory.newInstance();
		try {
			Transformer tx = factory.newTransformer(xsltSrc);
			tx.transform(inputSrc, outputSrc);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) {
		SignUpSheetDao dao = new SignUpSheetDao();

		/*
		// 1) create an organizer
		Organizer alice = new Organizer();
		alice.setFirstName("Alice");
		alice.setLastName("Wonderland");
		alice.setUsername("alice");
		alice.setPassword("queenofhearts");
		alice.setEmail("alice@wonderland.com");
		dao.register(alice);

		// 2) validate an organizer
		boolean userExists = dao.validate(1);
		if(userExists)
			System.out.println("User Exists");
		else
			System.out.println("User Does Not Exists");
		*/

		// 3) Create a sheet for an organizer ID with an address and display all sheets
		/*
		Address address = new Address();
		address.setStreet1("Las Canteras");
		address.setCity("Palm Spring");
		address.setState("CA");
		address.setZip("12344-4321");
		address.setStreet2(null);
		
		Sheet sheet = new Sheet();
		sheet.setDescription("Web Project Demo Description");
		sheet.setEventDate(new Date());
		sheet.setName("Web Project Demo");
		sheet.setWhere(address);
		
		address.setSheet(sheet);	// <-- ojo

		List<Sheet> sheets = dao.createSheet(1, sheet);
		
		for(Sheet s : sheets) {
			System.out.println(s.getName());
		}
		*/
		// 4) Get all sheets for an organizer
		/*
		List<Sheet> sheets = dao.getAllSheets(1);
		
		for(Sheet s : sheets) {
			System.out.println(s.getName());
		}
		*/
		// 5) Add TimeSlots to Sheet
		/*
		TimeSlot slot = new TimeSlot();
		slot.setNotes("My awsome project 2");
		slot.setSlotDate(new Date());
		slot.setWho("Team B");
		
		List<TimeSlot> slots = dao.addTimeSlot(3, slot);
		for(TimeSlot s : slots) {
			System.out.println(s.getNotes());
		}
		*/
		// 6) Remove TimeSlot from Sheet
		/*
		List<TimeSlot> slots = dao.deleteTimeSlot(3, 2);
		for(TimeSlot s : slots) {
			System.out.println(s.getNotes());
		}
		*/
		// 7) Update TimeSlot for Sheet
		/*
		TimeSlot slot = new TimeSlot();
		slot.setNotes("My awsome project 2 222");
		slot.setSlotDate(new Date());
		slot.setWho("Team C");
		
		List<TimeSlot> slots = dao.updateTimeSlot(3, 1, slot);
		for(TimeSlot s : slots) {
			System.out.println(s.getNotes());
		}
		*/
		// 8) Marshall organizer to XML
		/*
		Organizer alice = dao.getOrganizer(1);
		dao.exportToXml("xml/alice.xml", alice);
		*/
		// 9) Marshall all organizers to XML
//		/*
		List<Organizer> orgs = dao.getAllOrganizers();
		Organizers organizers = new Organizers();
		organizers.setOrganizers(orgs);
		dao.exportOrganizersToXml("xml/organizers.xml", organizers);	
	//	*/
		// 10) Unmarshall all organizers from XML
		/*
		Organizers orgs = dao.importOrganizersFromXml("xml/organizers2.xml");
		for(Organizer org : orgs.getOrganizers()) {
			System.out.println(org.getLastName());
			for(Sheet s : org.getSheets()) {
				System.out.println(s.getName());
				for(TimeSlot slot : s.getTimeSlots()) {
					System.out.print(slot.getNotes());
					System.out.println(slot.getSlotDate());
				}
			}
		}
		*/
		// 11) Transform 1
		dao.transform("xml/organizers2.xml", "xml/slots.xml", "xml/organizers2slots.xsl");
		// 12) Transform 2
		dao.transform("xml/organizers2.xml", "xml/organizers.html", "xml/organizers2html.xsl");
		
	}
}
