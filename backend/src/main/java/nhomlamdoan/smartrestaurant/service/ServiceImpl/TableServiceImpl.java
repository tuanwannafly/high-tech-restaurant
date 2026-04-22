package nhomlamdoan.smartrestaurant.service.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import nhomlamdoan.smartrestaurant.domain.Restaurant;
import nhomlamdoan.smartrestaurant.domain.RestaurantTable;
import nhomlamdoan.smartrestaurant.domain.constant.TableStatus;
import nhomlamdoan.smartrestaurant.domain.request.table.ReqTable;
import nhomlamdoan.smartrestaurant.domain.response.table.ResTable;
import nhomlamdoan.smartrestaurant.repository.RestaurantTableRepository;
import nhomlamdoan.smartrestaurant.service.TableService;
import nhomlamdoan.smartrestaurant.util.error.IdInvalidException;
import nhomlamdoan.smartrestaurant.util.error.PermissionException;

@Service
public class TableServiceImpl implements TableService {

    private final RestaurantTableRepository tableRepository;

    public TableServiceImpl(RestaurantTableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    // ------------------------------------------------------------------ public API

    @Override
    public ResTable createTable(ReqTable req, Restaurant restaurant) {
        RestaurantTable table = new RestaurantTable();
        table.setTableNumber(req.getTableNumber());
        table.setCapacity(req.getCapacity());
        table.setStatus(parseStatus(req.getStatus(), TableStatus.AVAILABLE));
        table.setRestaurant(restaurant);
        return toResTable(tableRepository.save(table));
    }

    @Override
    public ResTable updateTableWithOwnershipCheck(Long id, ReqTable req, Restaurant restaurant)
            throws PermissionException, IdInvalidException {

        RestaurantTable table = findAndVerifyOwnership(id, restaurant);

        if (req.getTableNumber() != null) table.setTableNumber(req.getTableNumber());
        if (req.getCapacity()    != null) table.setCapacity(req.getCapacity());
        if (req.getStatus()      != null) table.setStatus(parseStatus(req.getStatus(), table.getStatus()));

        return toResTable(tableRepository.save(table));
    }

    @Override
    public void deleteTableWithOwnershipCheck(Long id, Restaurant restaurant)
            throws PermissionException, IdInvalidException {

        findAndVerifyOwnership(id, restaurant);   // throws if not found / wrong restaurant
        tableRepository.deleteById(id);
    }

    @Override
    public List<ResTable> getAllTables(Restaurant restaurant) {
        return tableRepository.findByRestaurant(restaurant)
                .stream()
                .map(this::toResTable)
                .collect(Collectors.toList());
    }

    @Override
    public ResTable toResTable(RestaurantTable table) {
        ResTable res = new ResTable();
        res.setId(table.getTableId());
        res.setTableNumber(table.getTableNumber());
        res.setCapacity(table.getCapacity());
        res.setStatus(table.getStatus() != null ? table.getStatus().name() : null);
        return res;
    }

    // ------------------------------------------------------------------ helpers

    /**
     * Tìm bàn theo id và xác minh bàn thuộc nhà hàng hiện tại.
     * Ném PermissionException nếu bàn thuộc nhà hàng khác.
     */
    private RestaurantTable findAndVerifyOwnership(Long id, Restaurant restaurant)
            throws PermissionException, IdInvalidException {

        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Bàn không tồn tại với id = " + id));

        if (!table.getRestaurant().getRestaurantId().equals(restaurant.getRestaurantId())) {
            throw new PermissionException("Bàn không thuộc nhà hàng của bạn.");
        }
        return table;
    }

    private TableStatus parseStatus(String statusStr, TableStatus defaultStatus) {
        if (statusStr == null) return defaultStatus;
        try {
            return TableStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultStatus;
        }
    }
}
