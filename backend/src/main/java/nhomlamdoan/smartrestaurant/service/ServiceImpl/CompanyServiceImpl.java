package nhomlamdoan.smartrestaurant.service.ServiceImpl;


import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import nhomlamdoan.smartrestaurant.domain.Company;
import nhomlamdoan.smartrestaurant.domain.User;
import nhomlamdoan.smartrestaurant.domain.constant.CompanyStatus;
import nhomlamdoan.smartrestaurant.domain.request.company.CompanyRequest;
import nhomlamdoan.smartrestaurant.domain.response.ResultPaginationDTO;
import nhomlamdoan.smartrestaurant.domain.response.company.ResCompany;
import nhomlamdoan.smartrestaurant.repository.CompanyRepository;
import nhomlamdoan.smartrestaurant.repository.UserRepository;
import nhomlamdoan.smartrestaurant.service.CompanyService;

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
                .orElseThrow(() -> new RuntimeException("User not found"));


        Company company = new Company();
        company.setName(req.getName());           
        company.setAddress(req.getAddress());
        company.setPhone(req.getPhone());
        company.setEmail(req.getEmail());
        company.setStatus(CompanyStatus.INACTIVE);
        company.setUser(user);

        Company savedCompany = companyRepository.save(company);

        ResCompany res = new ResCompany();
        res.setCompanyId(savedCompany.getCompanyId());
        res.setName(savedCompany.getName());
        res.setAddress(savedCompany.getAddress());
        res.setPhone(savedCompany.getPhone());
        res.setEmail(savedCompany.getEmail());
        res.setStatus(CompanyStatus.INACTIVE);

        ResCompany.UserResponse userRes = new ResCompany.UserResponse();
        userRes.setId(user.getId());
        userRes.setName(user.getName());
        userRes.setEmail(user.getEmail());
        
        res.setUser(userRes);

        return res;
    }

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
        rs.setResult(pageCompany.getContent());
        return rs;
    }

    @Override
    public void deleteCompany(Long id) {
        this.companyRepository.deleteById(id);
    }

    @Override
    public Company handleUpdateCompany(Company c) {
        Optional<Company> companyOptional = this.companyRepository.findById(c.getId());
        if (companyOptional.isPresent()) {
            Company currentCompany = companyOptional.get();
            currentCompany.setLogo(c.getLogo());
            currentCompany.setName(c.getName());
            currentCompany.setDescription(c.getDescription());
            currentCompany.setAddress(c.getAddress());
            return this.companyRepository.save(currentCompany);
        }
        return null;
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
            // fetch all user belong to this company
            List<User> users = this.userRepository.findByCompany(com);
            this.userRepository.deleteAll(users);
        }

        this.companyRepository.deleteById(id);
    }
    
}