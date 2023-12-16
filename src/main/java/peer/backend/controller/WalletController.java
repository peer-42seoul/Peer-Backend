package peer.backend.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.wallet.CreateWalletItemRequest;
import peer.backend.dto.wallet.WalletItemResponse;
import peer.backend.entity.action.Wallet;
import peer.backend.service.action.WalletService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/wallet")
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public Page<WalletItemResponse> getWalletItemList(Pageable pageable) {
        Page<Wallet> walletList = this.walletService.getWalletList(pageable);
        return walletList.map(WalletItemResponse::new);
    }

    @PostMapping
    public void createWalletItem(@RequestBody @Valid CreateWalletItemRequest request) {
        this.walletService.saveWallet(request.getActionTypeName(), request.getWallet());
    }
}
