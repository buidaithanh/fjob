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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import vn.baymax.fjob.domain.Company;
import vn.baymax.fjob.dto.response.ResultPaginationDTO;
import vn.baymax.fjob.service.CompanyService;
import vn.baymax.fjob.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Company", description = "Company management APIs")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Operation(summary = "Create company", description = "Create a new company in the system", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Company created successfully", content = @Content(schema = @Schema(implementation = Company.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company c) {
        Company company = this.companyService.createCompany(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(company);
    }

    @Operation(summary = "Get all companies", description = "Retrieve list of companies with pagination, filtering and sorting", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Companies retrieved successfully", content = @Content(schema = @Schema(implementation = ResultPaginationDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompany(
            @Filter Specification<Company> specification, Pageable pageable) {
        return ResponseEntity.ok(this.companyService.getAllCompanies(specification, pageable));

    }

    @Operation(summary = "Update company", description = "Update existing company information", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company updated successfully", content = @Content(schema = @Schema(implementation = Company.class))),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company) {
        Company result = this.companyService.updateCompanyById(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Delete company", description = "Delete company by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        this.companyService.deleteCompanyById(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @Operation(summary = "Get company by ID", description = "Retrieve company details using company ID", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company retrieved successfully", content = @Content(schema = @Schema(implementation = Company.class))),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @GetMapping("/companies/{id}")
    @ApiMessage("Get company by id")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long id) {
        Optional<Company> companyOptional = this.companyService.getCompanyById(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(companyOptional.get());
    }

}
