package com.lokomanako.hack_api.api.ctrl;

import com.lokomanako.hack_api.api.dto.auth.AuthPack;
import com.lokomanako.hack_api.api.dto.auth.LogReq;
import com.lokomanako.hack_api.api.dto.auth.MeRes;
import com.lokomanako.hack_api.api.dto.auth.PwdRecReq;
import com.lokomanako.hack_api.api.dto.auth.RegReq;
import com.lokomanako.hack_api.api.dto.auth.SecQRes;
import com.lokomanako.hack_api.api.dto.auth.TokRes;
import com.lokomanako.hack_api.api.dto.common.ErrRes;
import com.lokomanako.hack_api.api.svc.AuthSvc;
import com.lokomanako.hack_api.api.util.CtxU;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Авторизация", description = "Регистрация, вход")
public class AuthCtl {

    @Autowired
    private AuthSvc authSvc;

    @Autowired
    private CtxU ctxU;

    @GetMapping("/recovery/questions")
    @Operation(summary = "РљРѕРЅС‚СЂРѕР»СЊРЅС‹Рµ РІРѕРїСЂРѕСЃС‹ РґР»СЏ РІРѕСЃСЃС‚Р°РЅРѕРІР»РµРЅРёСЏ")
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Р’РѕРїСЂРѕСЃС‹ РїРѕР»СѓС‡РµРЅС‹",
                    content = @Content(schema = @Schema(implementation = SecQRes.class))),
            @ApiResponse(responseCode = "429", description = "Р›РёРјРёС‚ Р·Р°РїСЂРѕСЃРѕРІ Рє auth endpoint",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public SecQRes q() {
        return authSvc.q();
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Регистрация")
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь зарегистрирован",
                    content = @Content(schema = @Schema(implementation = TokRes.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "409", description = "Логин уже занят",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "429", description = "Лимит запросов к auth endpoint",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public TokRes reg(@Valid @RequestBody RegReq req, jakarta.servlet.http.HttpServletResponse res) {
        AuthPack p = authSvc.reg(req);
        setRef(res, p);
        return new TokRes(p.getAccessToken(), p.getTokenType(), p.getExpiresIn());
    }

    @PostMapping("/login")
    @Operation(summary = "Вход")
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный вход",
                    content = @Content(schema = @Schema(implementation = TokRes.class))),
            @ApiResponse(responseCode = "401", description = "Неверный логин или пароль",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "423", description = "Аккаунт временно заблокирован",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "429", description = "Лимит запросов к auth endpoint",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public TokRes log(@Valid @RequestBody LogReq req, jakarta.servlet.http.HttpServletResponse res) {
        AuthPack p = authSvc.log(req);
        setRef(res, p);
        return new TokRes(p.getAccessToken(), p.getTokenType(), p.getExpiresIn());
    }

    @PostMapping("/recover-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Р’РѕСЃСЃС‚Р°РЅРѕРІР»РµРЅРёРµ РїР°СЂРѕР»СЏ РїРѕ email Рё РѕС‚РІРµС‚Р°Рј РЅР° РІРѕРїСЂРѕСЃС‹")
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "РџР°СЂРѕР»СЊ РѕР±РЅРѕРІР»РµРЅ"),
            @ApiResponse(responseCode = "400", description = "РћС€РёР±РєР° РІР°Р»РёРґР°С†РёРё",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "401", description = "РќРµРІРµСЂРЅС‹Рµ РґР°РЅРЅС‹Рµ РІРѕСЃСЃС‚Р°РЅРѕРІР»РµРЅРёСЏ",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "429", description = "Р›РёРјРёС‚ Р·Р°РїСЂРѕСЃРѕРІ Рє auth endpoint",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public void recPwd(@Valid @RequestBody PwdRecReq req) {
        authSvc.recPwd(req);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновление access token")
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Токен обновлен",
                    content = @Content(schema = @Schema(implementation = TokRes.class))),
            @ApiResponse(responseCode = "401", description = "Refresh token отсутствует или невалиден",
                    content = @Content(schema = @Schema(implementation = ErrRes.class))),
            @ApiResponse(responseCode = "429", description = "Лимит запросов к auth endpoint",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public TokRes ref(
            @Parameter(hidden = true) @CookieValue(name = "refreshToken", required = false) String rt,
            jakarta.servlet.http.HttpServletResponse res
    ) {
        AuthPack p = authSvc.ref(rt);
        setRef(res, p);
        return new TokRes(p.getAccessToken(), p.getTokenType(), p.getExpiresIn());
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход")
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Выход выполнен"),
            @ApiResponse(responseCode = "429", description = "Лимит запросов к auth endpoint",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public ResponseEntity<Void> out(
            @Parameter(hidden = true) @CookieValue(name = "refreshToken", required = false) String rt,
            jakarta.servlet.http.HttpServletResponse res
    ) {
        authSvc.out(rt);
        clearRef(res);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Текущий пользователь")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Профиль получен",
                    content = @Content(schema = @Schema(implementation = MeRes.class))),
            @ApiResponse(responseCode = "401", description = "Требуется access token",
                    content = @Content(schema = @Schema(implementation = ErrRes.class)))
    })
    public MeRes me() {
        return authSvc.me(ctxU.uid());
    }

    private void setRef(jakarta.servlet.http.HttpServletResponse res, AuthPack p) {
        ResponseCookie c = ResponseCookie.from("refreshToken", p.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(p.getRefreshSec())
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, c.toString());
    }

    private void clearRef(jakarta.servlet.http.HttpServletResponse res) {
        ResponseCookie c = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, c.toString());
    }
}
