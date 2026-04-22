package nhomlamdoan.smartrestaurant.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import nhomlamdoan.smartrestaurant.domain.Company;
import nhomlamdoan.smartrestaurant.domain.request.company.CompanyRequest;
import nhomlamdoan.smartrestaurant.domain.request.company.UpdateCompanyRequest;
import nhomlamdoan.smartrestaurant.domain.response.ResultPaginationDTO;
import nhomlamdoan.smartrestaurant.domain.response.company.ResCompany;
import nhomlamdoan.smartrestaurant.service.CompanyService;
import nhomlamdoan.smartrestaurant.util.annotation.ApiMessage;
import nhomlamdoan.smartrestaurant.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    @ApiMessage("Create a company")
    public ResponseEntity<ResCompany> createCompany(@Valid @RequestBody CompanyRequest company) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.companyService.handelCreateCompany(company));
    }

    @GetMapping("/companies")
    @ApiMessage("Fetch companies")
    public ResponseEntity<ResultPaginationDTO> getCompany(
            @Filter Specification<Company> spect, Pageable pageable) {
        return ResponseEntity.ok(this.companyService.handleGetCompany(spect, pageable));
    }

    // FIX: kiểm tra Optional trước khi .get() để tránh NoSuchElementException
    @GetMapping("/companies/{id}")
    @ApiMessage("Fetch company by id")
    public ResponseEntity<ResCompany> fetchCompanyById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Company> cOptional = this.companyService.findById(id);
        if (cOptional.isEmpty()) {
            throw new IdInvalidException("Company với id = " + id + " không tồn tại");
        }
        Company company = cOptional.get();
        // Reuse service mapping via a temporary ResCompany build
        ResCompany res = new ResCompany();
        res.setCompanyId(company.getCompanyId());
        res.setName(company.getName());
        res.setAddress(company.getAddress());
        res.setPhone(company.getPhone());
        res.setEmail(company.getEmail());
        res.setLogo(company.getLogo());
        res.setDescription(company.getDescription());
        res.setStatus(company.getStatus());
        if (company.getUser() != null) {
            ResCompany.UserResponse userRes = new ResCompany.UserResponse();
            userRes.setId(company.getUser().getId());
            userRes.setName(company.getUser().getName());
            userRes.setEmail(company.getUser().getEmail());
            res.setUser(userRes);
        }
        return ResponseEntity.ok(res);
    }

    // FIX: nhận UpdateCompanyRequest DTO thay vì raw Company entity
    @PutMapping("/companies")
    @ApiMessage("Update a company")
    public ResponseEntity<ResCompany> updateCompany(@Valid @RequestBody UpdateCompanyRequest reqCompany) {
        return ResponseEntity.ok(this.companyService.handleUpdateCompany(reqCompany));
    }

    @DeleteMapping("/companies/{id}")
    @ApiMessage("Delete a company")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.ok(null);
    }
}
