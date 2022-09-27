package com.example.initbackend.answer.service;

import com.example.initbackend.answer.domain.Answer;
import com.example.initbackend.answer.dto.IssueAnswerIdDto;
import com.example.initbackend.answer.dto.UpdateAnswerRequestDto;
import com.example.initbackend.answer.repository.AnswerRepository;
import com.example.initbackend.answer.vo.GetAnswerResponseVo;
import com.example.initbackend.answer.vo.GetAnswersTotalPageNumResponseVo;
import com.example.initbackend.answer.vo.IssueAnswerIdResponseVo;
import com.example.initbackend.comment.repository.CommentRepository;
import com.example.initbackend.global.handler.CustomException;
import com.example.initbackend.global.jwt.JwtTokenProvider;
import com.example.initbackend.global.jwt.JwtUtil;
import com.example.initbackend.global.response.ErrorCode;
import com.example.initbackend.question.repository.QuestionRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class AnswerService {

    private final JwtUtil jwtUtil;
    private final JwtTokenProvider jwtTokenProvider;

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;

    public IssueAnswerIdResponseVo issueAnswerId(HttpServletRequest request, IssueAnswerIdDto issueAnswerIdDto){
        String token = jwtTokenProvider.resolveAccessToken(request);
        Long userId  = jwtUtil.getPayloadByToken(token);
        Long questionId = issueAnswerIdDto.getQuestionId();
        Answer newAnswer = new Answer();
        newAnswer.setUserId(userId);
        newAnswer.setQuestionId(questionId);
        answerRepository.save(newAnswer);

        return new IssueAnswerIdResponseVo(newAnswer.getId());
    }


    public GetAnswerResponseVo getAnswer(Pageable pageable, Long questionId){
        Page<Answer> optionalAnswer = answerRepository.findAllByQuestionIdOrderByCreateDateDesc(questionId, pageable);
        GetAnswerResponseVo answers = new GetAnswerResponseVo(optionalAnswer.getContent());
        return answers;
    }

    public void updateAnswer(HttpServletRequest request, UpdateAnswerRequestDto updateAnswerRequestDto, Long answerId){

        String token = jwtTokenProvider.resolveAccessToken(request);
        Long userId  = jwtUtil.getPayloadByToken(token);

        Optional<Answer> optionalAnswer = answerRepository.findById(answerId);

        if (!optionalAnswer.isPresent()) {
            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
        }

        if (!userId.equals(optionalAnswer.get().getUserId())) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        optionalAnswer.ifPresent(selectAnswer ->{
                    selectAnswer.setContent(updateAnswerRequestDto.getContent());
                    answerRepository.save(selectAnswer);
        });

    }

//    @Transactional
    public void deleteAnswer(Long answerId){

        Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("");
//        EntityManager em = emf.createEntityManager();
//        EntityTransaction tx = em.getTransaction();
//        tx.begin();

//        try {
            optionalAnswer.ifPresentOrElse(
                    selectAnswer ->{
                        answerRepository.deleteById(answerId);
//                        commentRepository.deleteAllByAnswerId(answerId);
                    },
                    () -> {
                        throw new CustomException(ErrorCode.DATA_NOT_FOUND);
                    });
//            tx.commit();
//        } catch (Exception e){
//            tx.rollback();
//        }

    }


    public void selectAnswer(Long answerId){

        Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
        optionalAnswer.ifPresentOrElse(
                selectAnswer ->{
                    selectAnswer.setSelected(true);
                    answerRepository.save(selectAnswer);
                },
                () -> {
                    throw new CustomException(ErrorCode.DATA_NOT_FOUND);
                });

    }

    public GetAnswersTotalPageNumResponseVo getAnswersTotalPageNum(Pageable pageable, Long questionId){
        Page<Answer> optionalAnswer = answerRepository.findAllByQuestionIdOrderByCreateDateDesc(questionId, pageable);
        GetAnswersTotalPageNumResponseVo getAnswersTotalPageNumResponse = new GetAnswersTotalPageNumResponseVo(optionalAnswer.getTotalPages());
        return getAnswersTotalPageNumResponse;
    }

    public GetAnswerResponseVo getManagedAnswers(HttpServletRequest servletRequest, Pageable pageable){
        String token = jwtTokenProvider.resolveAccessToken(servletRequest);
        Long userId = JwtUtil.getPayloadByToken(token);

        Page<Answer> optionalAnswer = answerRepository.findAllByUserIdOrderByCreateDateDesc(userId, pageable);
        GetAnswerResponseVo answerList = new GetAnswerResponseVo(optionalAnswer.getContent());
        return answerList;
    }


    private boolean isDuplicatedAnswer(Long userId, Long questionId ) {
        return answerRepository.findByUserIdAndQuestionId(userId,questionId).isPresent();
    }
}
