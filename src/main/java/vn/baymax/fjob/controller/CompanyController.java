package vn.baymax.fjob.controller;

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
import vn.baymax.fjob.domain.Company;
import vn.baymax.fjob.dto.response.ResultPaginationDTO;
import vn.baymax.fjob.service.CompanyService;
import vn.baymax.fjob.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company c) {
        Company company = this.companyService.createCompany(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(company);
    }

    @GetMapping("/companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompany(
            @Filter Specification<Company> specification, Pageable pageable) {
        return ResponseEntity.ok(this.companyService.getAllCompanies(specification, pageable));

    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company) {
        Company result = this.companyService.updateCompanyById(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        this.companyService.deleteCompanyById(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/companies/{id}")
    @ApiMessage("Get company by id")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long id) {
        Optional<Company> companyOptional = this.companyService.getCompanyById(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(companyOptional.get());
    }

}
