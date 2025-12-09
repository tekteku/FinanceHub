package com.financehub.service;

import com.financehub.dto.InvestmentDTO;
import com.financehub.entity.*;
import com.financehub.exception.ResourceNotFoundException;
import com.financehub.repository.AccountRepository;
import com.financehub.repository.InvestmentRepository;
import com.financehub.repository.ProjectRepository;
import com.financehub.repository.TransactionRepository;
import com.financehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public InvestmentDTO invest(Long projectId, BigDecimal amount, Long userId, Long accountId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        
        User investor = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Account account = accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found or does not belong to user"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds in the selected account");
        }

        // Deduct from account
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        // Create Transaction record
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setType(Transaction.TransactionType.EXPENSE);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setDescription("Investment in project: " + project.getTitle());
        transaction.setPayee("FinanceHub Investments");
        transactionRepository.save(transaction);

        // Update project current amount
        project.setCurrentAmount(project.getCurrentAmount().add(amount));
        if (project.getCurrentAmount().compareTo(project.getTargetAmount()) >= 0) {
            project.setStatus(ProjectStatus.FUNDED);
        }
        projectRepository.save(project);

        Investment investment = new Investment();
        investment.setProject(project);
        investment.setInvestor(investor);
        investment.setAmount(amount);

        Investment savedInvestment = investmentRepository.save(investment);
        return mapToDTO(savedInvestment);
    }

    public List<InvestmentDTO> getMyInvestments(Long userId) {
        return investmentRepository.findByInvestorId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalInvestedAmount(Long userId) {
        return investmentRepository.findByInvestorId(userId).stream()
                .map(Investment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private InvestmentDTO mapToDTO(Investment investment) {
        InvestmentDTO dto = new InvestmentDTO();
        dto.setId(investment.getId());
        dto.setProjectId(investment.getProject().getId());
        dto.setProjectTitle(investment.getProject().getTitle());
        dto.setInvestorId(investment.getInvestor().getId());
        dto.setInvestorName(investment.getInvestor().getFullName());
        dto.setAmount(investment.getAmount());
        dto.setInvestedAt(investment.getInvestedAt());
        return dto;
    }
}
