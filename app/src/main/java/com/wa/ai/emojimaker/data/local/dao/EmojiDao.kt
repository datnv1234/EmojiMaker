package com.wa.ai.emojimaker.data.local.dao

import com.wa.ai.emojimaker.data.model.EmojiModel
import kotlin.coroutines.Continuation

/*@Metadata(
    d1 = ["\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\bg\u0018\u00002\u00020\u0001J\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H§@ø\u0001\u0000¢\u0006\u0002\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0004H'J\u0011\u0010\t\u001a\u00020\nH@ø\u0001\u0000¢\u0006\u0002\u0010\u0005J\u0010\u0010\u000b\u001a\u00020\u00072\u0006\u0010\u000c\u001a\u00020\rH'\u0002\u0004\n\u0002\b\u0019¨\u0006\u000e"],
    d2 = ["Lcom/emojimaker/emojistitch/database/EmojiDao;", "", "getAllEmoji", "", "Lcom/emojimaker/emojistitch/data/model/EmojiModel;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertEmoji", "", "emojiModel", "isEmojiExisted", "", "updateUnLockShowAds", "id", "", "EmojiMaker1.0.3_04.03.2024_release"],
    k = 1,
    mv = [1, 7, 1],
    xi = 48
)*/ /* compiled from: EmojiDao.kt */
interface EmojiDao {
    fun getAllEmoji(continuation: Continuation<MutableList<EmojiModel?>?>?): Any?
    fun insertEmoji(emojiModel: EmojiModel?)
    fun isEmojiExisted(continuation: Continuation<Boolean?>?): Any?
    fun updateUnLockShowAds(i2: Int)

//    @Metadata(k = 3, mv = [1, 7, 1], xi = 48) /* compiled from: EmojiDao.kt */
    object DefaultImpls {
        /* JADX WARNING: Removed duplicated region for block: B:12:0x0032  */ /* JADX WARNING: Removed duplicated region for block: B:8:0x0024  */ /* Code decompiled incorrectly, please refer to instructions dump. */
        fun isEmojiExisted(r4: EmojiDao?, r5: Continuation<Boolean?>?): Any {
            /*
                boolean r0 = r5 instanceof com.emojimaker.emojistitch.database.EmojiDao$isEmojiExisted$1
                if (r0 == 0) goto L_0x0014
                r0 = r5
                com.emojimaker.emojistitch.database.EmojiDao$isEmojiExisted$1 r0 = (com.emojimaker.emojistitch.database.EmojiDao$isEmojiExisted$1) r0
                int r1 = r0.label
                r2 = -2147483648(0xffffffff80000000, float:-0.0)
                r1 = r1 & r2
                if (r1 == 0) goto L_0x0014
                int r5 = r0.label
                int r5 = r5 - r2
                r0.label = r5
                goto L_0x0019
            L_0x0014:
                com.emojimaker.emojistitch.database.EmojiDao$isEmojiExisted$1 r0 = new com.emojimaker.emojistitch.database.EmojiDao$isEmojiExisted$1
                r0.<init>(r5)
            L_0x0019:
                java.lang.Object r5 = r0.result
                java.lang.Object r1 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
                int r2 = r0.label
                r3 = 1
                if (r2 == 0) goto L_0x0032
                if (r2 != r3) goto L_0x002a
                kotlin.ResultKt.throwOnFailure(r5)
                goto L_0x003e
            L_0x002a:
                java.lang.IllegalStateException r4 = new java.lang.IllegalStateException
                java.lang.String r5 = "call to 'resume' before 'invoke' with coroutine"
                r4.<init>(r5)
                throw r4
            L_0x0032:
                kotlin.ResultKt.throwOnFailure(r5)
                r0.label = r3
                java.lang.Object r5 = r4.getAllEmoji(r0)
                if (r5 != r1) goto L_0x003e
                return r1
            L_0x003e:
                java.util.Collection r5 = (java.util.Collection) r5
                boolean r4 = r5.isEmpty()
                r4 = r4 ^ r3
                java.lang.Boolean r4 = kotlin.coroutines.jvm.internal.Boxing.boxBoolean(r4)
                return r4
            */
            throw UnsupportedOperationException("Method not decompiled: com.emojimaker.emojistitch.database.EmojiDao.DefaultImpls.isEmojiExisted(com.emojimaker.emojistitch.database.EmojiDao, kotlin.coroutines.Continuation):java.lang.Object")
        }
    }
}
