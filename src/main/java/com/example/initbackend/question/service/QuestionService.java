package com.example.initbackend.question.service;

import com.example.initbackend.question.domain.Question;
import com.example.initbackend.question.dto.IssueQuestionIdRequestDto;
import com.example.initbackend.question.repository.QuestionRepository;
import com.example.initbackend.question.vo.IssueQuestionIdResponseVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public IssueQuestionIdResponseVo issueQuestionId(Integer userId){
        Question question = IssueQuestionIdRequestDto.toEntity(userId);
        Question newQuestion = questionRepository.save(question);

        IssueQuestionIdResponseVo issueQuestionIdResponse = new IssueQuestionIdResponseVo(newQuestion.getId());

        return issueQuestionIdResponse;
    }
}
