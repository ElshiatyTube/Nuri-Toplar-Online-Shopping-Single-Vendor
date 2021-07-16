package com.eatitappclient.tws.Callback;

import com.eatitappclient.tws.Model.CommentModel;

import java.util.List;

public interface iCommentCallbackListener {
    void oniCommentLoadSuccess(List<CommentModel> commentModels);
    void oniCommentLoadFailed(String message);
}
