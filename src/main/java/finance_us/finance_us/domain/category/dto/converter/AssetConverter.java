package finance_us.finance_us.domain.category.dto.converter;

import finance_us.finance_us.domain.category.dto.AssetRequestDto;
import finance_us.finance_us.domain.category.dto.AssetResponseDto;
import finance_us.finance_us.domain.category.entity.MainAsset;
import finance_us.finance_us.domain.category.entity.SubAsset;
import finance_us.finance_us.domain.user.entity.User;

public class AssetConverter
{

    // 메인 자산 요청 dto를 Entity로 변환
    static public MainAsset mainAssetRequestDtoToEntity(Long userId, AssetRequestDto.MainRequestDto requestDto)
    {
        // 서브 자산 엔티티 리스트를 첨부해준다.
        var subAssetEntityList = requestDto.getSubAssets().stream().map(item -> subAssetRequestDtoToEntity(userId, item)).toList();
        // 유저번호만 넣어서 유저를 넣어줌.
        var user = User.builder().Id(userId).build();

        return MainAsset.builder()
                .mainName(requestDto.getName())
                .user(user)
                .subAssets(subAssetEntityList)
                .build();

    }

    static public SubAsset subAssetRequestDtoToEntity(Long userId, AssetRequestDto.SubRequestDto requestDto)
    {

        // 유저번호만 넣어서 유저 첨부
        var user = User.builder().Id(userId).build();

        return SubAsset.builder()
                .subName(requestDto.getName())
                .user(user)
                .build();

    }


    // 메인 자산 Entity를 응답dto로 전환
    static public AssetResponseDto.MainResponseDto mainAssetEntityToDto(MainAsset mainAsset)
    {
        // 서브 자산을 dto로 전환 후 목록화
        var subAssetDtoList = mainAsset.getSubAssets().stream().map(AssetConverter::subAssetEntityToDto).toList();

        return AssetResponseDto.MainResponseDto.builder()
                .id(mainAsset.getId())
                .name(mainAsset.getMainName())
                .subAssets(subAssetDtoList)
                .build();

    }

    // 서브 자산 Entity를 응답dto로 전환
    static public AssetResponseDto.SubResponseDto subAssetEntityToDto(SubAsset subAsset)
    {
        return AssetResponseDto.SubResponseDto.builder()
                .id(subAsset.getId())
                .name(subAsset.getSubName())
                .build();
    }

}
