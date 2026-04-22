package nhomlamdoan.smartrestaurant.service.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import nhomlamdoan.smartrestaurant.domain.Menu;
import nhomlamdoan.smartrestaurant.domain.MenuItem;
import nhomlamdoan.smartrestaurant.domain.Restaurant;
import nhomlamdoan.smartrestaurant.domain.constant.MenuItemStatus;
import nhomlamdoan.smartrestaurant.domain.constant.MenuStatus;
import nhomlamdoan.smartrestaurant.domain.request.menuitem.ReqMenuItem;
import nhomlamdoan.smartrestaurant.domain.response.menuitem.ResMenuItem;
import nhomlamdoan.smartrestaurant.repository.MenuItemRepository;
import nhomlamdoan.smartrestaurant.repository.MenuRepository;
import nhomlamdoan.smartrestaurant.service.MenuItemService;
import nhomlamdoan.smartrestaurant.util.error.IdInvalidException;
import nhomlamdoan.smartrestaurant.util.error.PermissionException;

@Service
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuRepository menuRepository;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository,
                               MenuRepository menuRepository) {
        this.menuItemRepository = menuItemRepository;
        this.menuRepository = menuRepository;
    }

    // ------------------------------------------------------------------ public API

    @Override
    public ResMenuItem createMenuItem(ReqMenuItem req, Restaurant restaurant) {
        Menu menu = findOrCreateMenu(req.getCategory(), restaurant);

        MenuItem item = new MenuItem();
        item.setName(req.getName());
        item.setDescription(req.getDescription());
        item.setPrice(req.getPrice());
        item.setImageUrl(req.getImageUrl());
        item.setStatus(MenuItemStatus.AVAILABLE);
        item.setMenu(menu);

        return toResMenuItem(menuItemRepository.save(item));
    }

    @Override
    public ResMenuItem updateMenuItemWithOwnershipCheck(Long id, ReqMenuItem req,
                                                        Restaurant restaurant)
            throws PermissionException, IdInvalidException {

        MenuItem item = findAndVerifyOwnership(id, restaurant);

        if (req.getName()        != null) item.setName(req.getName());
        if (req.getDescription() != null) item.setDescription(req.getDescription());
        if (req.getPrice()       != null) item.setPrice(req.getPrice());
        if (req.getImageUrl()    != null) item.setImageUrl(req.getImageUrl());

        if (req.getCategory() != null) {
            Menu menu = findOrCreateMenu(req.getCategory(), restaurant);
            item.setMenu(menu);
        }

        return toResMenuItem(menuItemRepository.save(item));
    }

    @Override
    public void deleteMenuItemWithOwnershipCheck(Long id, Restaurant restaurant)
            throws PermissionException, IdInvalidException {

        findAndVerifyOwnership(id, restaurant);   // throws if not found / wrong restaurant
        menuItemRepository.deleteById(id);
    }

    @Override
    public List<ResMenuItem> getAllMenuItems(Restaurant restaurant) {
        return menuItemRepository.findByMenuRestaurant(restaurant)
                .stream()
                .map(this::toResMenuItem)
                .collect(Collectors.toList());
    }

    @Override
    public ResMenuItem toResMenuItem(MenuItem item) {
        ResMenuItem res = new ResMenuItem();
        res.setId(item.getItemId());
        res.setName(item.getName());
        res.setDescription(item.getDescription());
        res.setPrice(item.getPrice());
        res.setImageUrl(item.getImageUrl());
        res.setStatus(item.getStatus() != null ? item.getStatus().name() : null);
        if (item.getMenu() != null) {
            res.setCategory(item.getMenu().getName());
        }
        return res;
    }

    // ------------------------------------------------------------------ helpers

    /**
     * Tìm MenuItem theo id và xác minh nó thuộc nhà hàng hiện tại.
     */
    private MenuItem findAndVerifyOwnership(Long id, Restaurant restaurant)
            throws PermissionException, IdInvalidException {

        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException(
                        "MenuItem không tồn tại với id = " + id));

        // MenuItem → Menu → Restaurant
        if (item.getMenu() == null || item.getMenu().getRestaurant() == null
                || !item.getMenu().getRestaurant().getRestaurantId()
                        .equals(restaurant.getRestaurantId())) {
            throw new PermissionException("Món ăn không thuộc nhà hàng của bạn.");
        }
        return item;
    }

    /**
     * Tìm hoặc tạo Menu theo tên danh mục trong phạm vi nhà hàng.
     */
    private Menu findOrCreateMenu(String categoryName, Restaurant restaurant) {
        String name = (categoryName == null || categoryName.isBlank()) ? "Chung" : categoryName;
        return menuRepository.findByNameAndRestaurant(name, restaurant)
                .orElseGet(() -> {
                    Menu newMenu = new Menu();
                    newMenu.setName(name);
                    newMenu.setDescription("Danh mục: " + name);
                    newMenu.setStatus(MenuStatus.ACTIVE);
                    newMenu.setRestaurant(restaurant);
                    return menuRepository.save(newMenu);
                });
    }
}
