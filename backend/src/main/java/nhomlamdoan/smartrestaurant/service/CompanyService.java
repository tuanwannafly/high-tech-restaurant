package nhomlamdoan.smartrestaurant.service;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import nhomlamdoan.smartrestaurant.domain.Company;
import nhomlamdoan.smartrestaurant.domain.request.company.CompanyRequest;
import nhomlamdoan.smartrestaurant.domain.request.company.UpdateCompanyRequest;
import nhomlamdoan.smartrestaurant.domain.response.ResultPaginationDTO;
import nhomlamdoan.smartrestaurant.domain.response.company.ResCompany;

@Service
public interface CompanyService {
    ResCompany handelCreateCompany(CompanyRequest company);

    ResultPaginationDTO handleGetCompany(Specification<Company> spec, Pageable pageable);

    void deleteCompany(Long id);

    ResCompany handleUpdateCompany(UpdateCompanyRequest req);

    Optional<Company> findById(long id);

    void handleDeleteCompany(long id);
}
