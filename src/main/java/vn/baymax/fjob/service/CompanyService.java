package vn.baymax.fjob.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import vn.baymax.fjob.domain.Company;
import vn.baymax.fjob.domain.User;
import vn.baymax.fjob.dto.response.ResultPaginationDTO;
import vn.baymax.fjob.repository.CompanyRepository;
import vn.baymax.fjob.repository.UserRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }

    public void deleteCompanyById(long id) {
        Optional<Company> comOptional = this.companyRepository.findById(id);
        if (comOptional.isPresent()) {
            Company com = comOptional.get();
            List<User> users = this.userRepository.findByCompany(com);
            this.userRepository.deleteAll(users);
        }
        companyRepository.deleteById(id);
    }

    public Optional<Company> getCompanyById(long id) {
        return this.companyRepository.findById(id);
    }

    public List<Company> getAllCompanies() {
        return this.companyRepository.findAll();
    }

    public ResultPaginationDTO getAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageCompany.getTotalPages());
        mt.setTotal(pageCompany.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageCompany.getContent());
        return rs;
    }

    public Company updateCompanyById(Company c) {
        Optional<Company> companyObOptional = companyRepository.findById(c.getId());

        if (companyObOptional.isPresent()) {
            Company company = companyObOptional.get();
            if (c.getName() != null) {
                company.setName(c.getName());
            }
            if (c.getAddress() != null) {
                company.setAddress(c.getAddress());
            }
            if (c.getDescription() != null) {
                company.setDescription(c.getDescription());
            }
            if (c.getLogo() != null) {
                company.setLogo(c.getLogo());
            }
            return this.companyRepository.save(company);
        }
        return null;

    }

}
