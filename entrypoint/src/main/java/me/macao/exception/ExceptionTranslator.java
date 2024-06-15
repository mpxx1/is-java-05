package me.macao.exception;

import me.macao.msdto.reply.ErrMap;

public class ExceptionTranslator {

    public static void exec(ErrMap map)
            throws DataTransferException,
                EmailCreateException, InvalidOperationException,
                ObjectNotFoundException, PasswordCreateException {

        throw switch (map.reason()) {
            case "Email create"         -> new EmailCreateException(map.message());
            case "Password create"      -> new PasswordCreateException(map.message());
            case "Invalid operation"    -> new InvalidOperationException(map.message());
            case "Object not found"     -> new ObjectNotFoundException(map.message());
            default                     -> new DataTransferException(map.message());
        };
    }
}
