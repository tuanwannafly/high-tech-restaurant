package nhomlamdoan.smartrestaurant.service.ServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import nhomlamdoan.smartrestaurant.domain.Company;
import nhomlamdoan.smartrestaurant.domain.User;
import nhomlamdoan.smartrestaurant.domain.constant.CompanyStatus;
import nhomlamdoan.smartrestaurant.domain.request.company.CompanyRequest;
import nhomlamdoan.smartrestaurant.domain.request.company.UpdateCompanyRequest;
import nhomlamdoan.smartrestaurant.domain.response.ResultPaginationDTO;
import nhomlamdoan.smartrestaurant.domain.response.company.ResCompany;
import nhomlamdoan.smartrestaurant.repository.CompanyRepository;
import nhomlamdoan.smartrestaurant.repository.UserRepository;
import nhomlamdoan.smartrestaurant.service.CompanyService;
import nhomlamdoan.smartrestaurant.util.error.IdInvalidException;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResCompany handelCreateCompany(CompanyRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + req.getUserId()));

        Company company = new Company();
        company.setName(req.getName());
        company.setAddress(req.getAddress());
        company.setPhone(req.getPhone());
        company.setEmail(req.getEmail());
        company.setLogo(req.getLogo());
        company.setDescription(req.getDescription());
        company.setStatus(CompanyStatus.INACTIVE);
        company.setUser(user);

        Company savedCompany = companyRepository.save(company);
        return mapToResCompany(savedCompany);
    }

    // FIX 1: handleGetCompany trả về ResCompany thay vì raw Company entity
    @Override
    public ResultPaginationDTO handleGetCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageCompany.getNumber() + 1);
        mt.setPageSize(pageCompany.getSize());
        mt.setPages(pageCompany.getTotalPages());
        mt.setTotal(pageCompany.getTotalElements());

        rs.setMeta(mt);
        // Map sang ResCompany để tránh Jackson infinite loop và lộ dữ liệu nhạy cảm
        rs.setResult(pageCompany.getContent().stream()
                .map(this::mapToResCompany)
                .collect(Collectors.toList()));
        return rs;
    }

    @Override
    public void deleteCompany(Long id) {
        this.companyRepository.deleteById(id);
    }

    // FIX 2: handleUpdateCompany nhận UpdateCompanyRequest thay vì raw Company entity
    @Override
    public ResCompany handleUpdateCompany(UpdateCompanyRequest req) {
        Company currentCompany = this.companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + req.getCompanyId()));

        if (req.getName() != null)        currentCompany.setName(req.getName());
        if (req.getLogo() != null)        currentCompany.setLogo(req.getLogo());
        if (req.getDescription() != null) currentCompany.setDescription(req.getDescription());
        if (req.getAddress() != null)     currentCompany.setAddress(req.getAddress());
        if (req.getPhone() != null)       currentCompany.setPhone(req.getPhone());
        if (req.getEmail() != null)       currentCompany.setEmail(req.getEmail());

        return mapToResCompany(this.companyRepository.save(currentCompany));
    }

    @Override
    public Optional<Company> findById(long id) {
        return this.companyRepository.findById(id);
    }

    @Override
    public void handleDeleteCompany(long id) {
        Optional<Company> comOptional = this.companyRepository.findById(id);
        if (comOptional.isPresent()) {
            Company com = comOptional.get();
            List<User> users = this.userRepository.findByCompany(com);
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }

    // Helper: map Company entity → ResCompany DTO
    private ResCompany mapToResCompany(Company company) {
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
        return res;
    }
}
