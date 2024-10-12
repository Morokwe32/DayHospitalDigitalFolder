package za.ac.cput.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.CreateMedicalHistory;
import za.ac.cput.domain.Patient;
import za.ac.cput.domain.PharmacyQueue;
import za.ac.cput.domain.Staff;
import za.ac.cput.service.MedicalHistoryService;
import za.ac.cput.service.PatientService;
import za.ac.cput.service.PharmacyQueueService;
import za.ac.cput.service.StaffService;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class StaffController {

    @Autowired
    private StaffService staffService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private PharmacyQueueService pharmacyQueueService;
    @Autowired
    private MedicalHistoryService medicalHistoryService;
    List<CreateMedicalHistory> mhList;

    @GetMapping("/showHistoryPage")
        public String test(Model model){
        model.addAttribute("viewMedicalHistory", new Staff());
        return  "ViewMedicalHistory";
        }


    @GetMapping("/populateMedicalHistory")
    public String showHistory(@RequestParam String patientId,Model model){
        mhList = medicalHistoryService.getAllMedicalHistory(patientId);
        model.addAttribute("historyList", mhList);
        System.out.println(mhList.toString());
        return  "showPatientHistory";
    }

    @GetMapping("/showPh")
    public String showData(Model model){
        return  "redirect:/showPatientHistory";
    }

    @GetMapping("/createMedicalHistory")
    public String createHistory(@RequestParam String patientId, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String testResults, @RequestParam String allergies, @RequestParam String prescribedMedication, @RequestParam String treatmentPlan) {
        CreateMedicalHistory medicalHistory = new CreateMedicalHistory();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        medicalHistory.setPatientIdMh(patientId);
        medicalHistory.setFirstNameMh(firstName);
        medicalHistory.setLastNameMh(lastName);
        medicalHistory.setTestResultsMh(testResults);
        medicalHistory.setAllergiesMh(allergies);
        medicalHistory.setPrescribedMedicationMh(prescribedMedication);
        medicalHistory.setTreatmentPlanMh(treatmentPlan);
        medicalHistory.setDateCreateMh(formatter.format(date).toString());
        medicalHistoryService.create(medicalHistory);
        PharmacyQueue pharmacyQueue = new PharmacyQueue();
        if(pharmacyQueueService.checkQueue(patientId)==true){
            return "existInQueue";
        }
        pharmacyQueue.setPatientId(patientId);
        pharmacyQueue.setFirstName(firstName);
        pharmacyQueue.setLastName(lastName);
        pharmacyQueue.setPrescribedMedication(prescribedMedication);
        pharmacyQueue.setTreatmentPlan(treatmentPlan);
        pharmacyQueueService.createPharmacyQueue(pharmacyQueue);
        return "doctorView";
    }


    @GetMapping("/login")
    public String showForm(Model model) {
        model.addAttribute("staff", new Staff());
        return "staff";
    }



    @GetMapping("/loginCredentials")
    public String showLogin(Model model) {
        model.addAttribute("loginCredentials", new Staff());
        return "loginCredentials";
    }

    @GetMapping("/getFolderDetails")
    public String showDetails(@RequestParam String patientId, Model model) {
        Patient patient = patientService.getDetailsOfPatient(patientId);
        model.addAttribute("patient", patient);
        return "doctorView";
    }

    @GetMapping("/navigateToLogin")
    public String showLoginPage() {
        return "loginCredentials";
    }


    @PostMapping("/save")
    public String saveStaff(Staff staff) {
        staffService.create((staff));
        return "redirect:/loginCredentials";

    }

    @PostMapping("/login")
    public String validateLogin(@RequestParam String id, @RequestParam String role, @RequestParam String name, @RequestParam String password, Model model) {
        boolean isValidLogin = staffService.validateLogin(name, role, id, password);
        if (isValidLogin) {
            if (role.equals("Doctor")) {
                return "doctorView";
            } else if (role.equals("Pharmacist")) {
                return "redirect:/pharmacyQueues";
            } else if (role.equals("Receptionist")) {
                model.addAttribute("patient", new Patient());
                return "redirect:/receptionist";
            } else {
                return "incorrect";
            }
        } else {
            return "incorrect";
        }
    }




    @PostMapping("/savePatient")
    public String savePatient(Patient patient){
        patientService.create(patient);
        return "receptionistView";
    }

   /* @PostMapping("/assignToQueue")
    public String assign(@RequestParam String patientId, @RequestParam String firstName,@RequestParam String lastName,@RequestParam String prescribedMedication,@RequestParam String treatmentPlan){
        PharmacyQueue pharmacyQueue = new PharmacyQueue();
        pharmacyQueue.setPatientId(patientId);
        pharmacyQueue.setFirstName(firstName);
        pharmacyQueue.setLastName(lastName);
        pharmacyQueue.setPrescribedMedication(prescribedMedication);
        pharmacyQueue.setTreatmentPlan(treatmentPlan);
        pharmacyQueueService.createPharmacyQueue(pharmacyQueue);
        return "doctorView";
    }*/

    @GetMapping("/pharmacy")
    public String showPharmacyPage() {
        return "pharmacistView";
    }

/*
    @GetMapping("/getPharmacyQueue")
    public String showDetailsInQueue(String patientId, Model model) {
        Patient patient = patientService.getDetailsOfPatient(patientId);
        model.addAttribute("patient", patient);
        return "doctorView";
    }*/

    @GetMapping("/pharmacyQueues")
    public String getAllPharmacyQueues(Model model) {
        List<PharmacyQueue> pharmacyQueues = pharmacyQueueService.getAllPharmacyQueues();
        model.addAttribute("pharmacyQueues", pharmacyQueues);
        return "pharmacistView";
    }
    @GetMapping("/showPatientDetails")
    public String showPatientDetails(@RequestParam String patientId, Model model) {
        PharmacyQueue pharmacyQueue = pharmacyQueueService.showTreatment(patientId);
        String treatment = pharmacyQueue.getTreatmentPlan();
        String medication = pharmacyQueue.getPrescribedMedication();
        model.addAttribute("treatment", treatment);
        model.addAttribute("medication", medication);
        return "medicationTreatment";
    }

    @GetMapping("/createPatient")
    public String showCreatePatient(Model model){
        model.addAttribute("patient", new Patient());
        return "createPatientFolder";
    }

    @GetMapping("/showPatients")
    public String showPatientPage(Model model){
        List<Patient> patients = patientService.getAll();

        model.addAttribute("patientList", patients);

        return "viewPatientFolder";
    }

}






