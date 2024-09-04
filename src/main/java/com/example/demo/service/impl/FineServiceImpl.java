package com.example.demo.service.impl;

import com.example.demo.model.BorrowDetail;
import com.example.demo.model.BorrowForm;
import com.example.demo.model.Fine;
import com.example.demo.repository.BorrowDetailRepository;
import com.example.demo.repository.BorrowFormRepository;
import com.example.demo.repository.FineRepository;
import com.example.demo.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class FineServiceImpl implements FineService {

    @Autowired
    private BorrowFormRepository borrowFormRepository;

    @Autowired
    private BorrowDetailRepository borrowDetailRepository;

    @Autowired
    private FineRepository fineRepository;

    @Override
    @Transactional
    public void calculateFines() {
        // Lấy danh sách các phiếu mượn đang trong trạng thái BORROWED hoặc PRE_RETURNED
        List<BorrowForm> overdueBorrowForms = borrowFormRepository.findByStatusIn(Arrays.asList("BORROWED", "PRE_RETURNED"));

        for (BorrowForm borrowForm : overdueBorrowForms) {
            calculateFineForBorrowForm(borrowForm);
        }
    }

    @Override
    @Transactional
    public void calculateFineForBorrowForm(BorrowForm borrowForm) {
        // Kiểm tra ngày hết hạn của phiếu mượn
        LocalDate dueDate = borrowForm.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();

        if (now.isAfter(dueDate)) {
            // Tính số ngày quá hạn
            long overdueDays = ChronoUnit.DAYS.between(dueDate, now);

            // Lấy chi tiết mượn sách
            List<BorrowDetail> borrowDetails = borrowDetailRepository.findByBorrowRecordId(borrowForm.getId());

            for (BorrowDetail borrowDetail : borrowDetails) {
                // Chỉ tính phạt cho các sách chưa được trả
                if (borrowDetail.getQuantity() > 0) {
                    BigDecimal penaltyAmount = BigDecimal.valueOf(overdueDays * 5000L * borrowDetail.getQuantity());

                    // Tạo bản ghi phạt mới nếu chưa tồn tại cho ngày hiện tại
                    Optional<Fine> existingFine = fineRepository.findByBorrowRecordIdAndPenaltyDate(borrowForm.getId(), now);
                    if (existingFine.isEmpty()) {
                        Fine fine = new Fine();
                        fine.setBorrowRecordId(borrowForm.getId());
                        fine.setPenaltyAmount(penaltyAmount);
                        fine.setPenaltyDate(now);
                        fineRepository.save(fine);
                    } else {
                        // Nếu đã có bản ghi phạt, cộng dồn tiền phạt
                        Fine fine = existingFine.get();
                        fine.setPenaltyAmount(fine.getPenaltyAmount().add(penaltyAmount));
                        fineRepository.save(fine);
                    }
                }
            }
        }
    }
}

