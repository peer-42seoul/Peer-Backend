package peer.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.wallet.WalletItemResponse;
import peer.backend.entity.action.Wallet;
import peer.backend.service.action.WalletService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/wallet")
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    Page<WalletItemResponse> getWalletItemList(Pageable pageable) {
        Page<Wallet> walletList = this.walletService.getWalletList(pageable);
        return walletList.map(WalletItemResponse::new);
    }
}
