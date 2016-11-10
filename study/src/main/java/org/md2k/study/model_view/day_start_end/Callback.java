package org.md2k.study.model_view.day_start_end;

import org.md2k.datakitapi.exception.DataKitException;

/**
 * Created by monowar on 3/13/16.
 */
interface Callback {
    void onResponse(String response) throws DataKitException;
}
